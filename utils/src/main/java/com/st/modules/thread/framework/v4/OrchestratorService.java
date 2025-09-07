package com.st.modules.thread.framework.v4;



import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/** 编排层：提供三种语义的聚合接口 + 结构化结果 + 统计 + SSE 推送 */
public class OrchestratorService {

    private final Engine engine;
    private final TicketStore store;

    public OrchestratorService(Engine engine, TicketStore store){
        this.engine = engine; this.store = store;
    }

    /* ====================== 开放式有损聚合（无全局超时，不失败） ====================== */
    public Map<String,Object> multiSmartAggregateOpenEnded(List<String> names, long waitMs, Long perTaskTimeoutMs) {

        final Ticket ticket = new Ticket(java.util.UUID.randomUUID().toString(), names, false);
        ticket.status = Ticket.Status.PROCESSING; store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);

        final List<CompletableFuture<ResultItem>> resultCfs = new ArrayList<CompletableFuture<ResultItem>>(names.size());

        for (final String name : names) {
            final Ticket.Subtask st = ticket.subtasks.get(name);
            st.status = "RUNNING"; Ticket.advanceStage(st, Ticket.Stage.PREPARE); store.publishUpdate(ticket.taskId, ticket);

            Supplier<String> sup = new Supplier<String>() {
                public String get() {
                    Ticket.advanceStage(st, Ticket.Stage.CALL_DOWNSTREAM); store.publishUpdate(ticket.taskId, ticket);
                    String v = doBusiness(ticket.taskId, ticket, name, st); // 你的真实业务；示例在下方
                    Ticket.advanceStage(st, Ticket.Stage.MERGE); store.publishUpdate(ticket.taskId, ticket);
                    return v;
                }
            };

            final boolean[] degraded = new boolean[]{ false };

            CompletableFuture<String> inner;
            if (perTaskTimeoutMs == null) {
                inner = engine.supplyNoTimeout(sup, new Supplier<String>() {
                    public String get(){ degraded[0]=true; st.error="exception/fallback"; return "fallback:"+name; }
                });
            } else {
                inner = engine.supply(sup, perTaskTimeoutMs, TimeUnit.MILLISECONDS,
                        new Supplier<String>() { public String get(){ degraded[0]=true; st.error="timeout/fallback"; return "fallback:"+name; }},
                        false // 到点不打断线程
                );
            }

            CompletableFuture<ResultItem> result = new CompletableFuture<ResultItem>();
            inner.whenComplete(new BiConsumer<String, Throwable>() {
                public void accept(String val, Throwable err) {
                    ResultItem item;
                    if (err == null) {
                        if (degraded[0]) { st.status="FALLBACK"; item = ResultItem.fallback(name, val, st.error); }
                        else { st.status="SUCCESS"; item = ResultItem.success(name, val); }
                    } else {
                        st.status="FALLBACK"; st.error=shortErr(err); item = ResultItem.fallback(name, "fallback:"+name, st.error);
                    }
                    Ticket.advanceStage(st, Ticket.Stage.FINISH); st.endedAt=System.currentTimeMillis();
                    result.complete(item);
                    ticket.recomputeAggregates(); store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
                }
            });

            resultCfs.add(result);
        }

        CompletableFuture<List<ResultItem>> combined =
                CompletableFuture.allOf(resultCfs.toArray(new CompletableFuture[0]))
                        .thenApply(new java.util.function.Function<Void, List<ResultItem>>() {
                            public List<ResultItem> apply(Void v){
                                List<ResultItem> out = new ArrayList<ResultItem>(resultCfs.size());
                                for (CompletableFuture<ResultItem> cf : resultCfs) out.add(cf.join());
                                return out;
                            }
                        });

        try {
            List<ResultItem> data = combined.get(waitMs, TimeUnit.MILLISECONDS);
            ticket.mode="SYNC"; ticket.status=Ticket.Status.SUCCESS; ticket.data=data; ticket.progress=100; ticket.recomputeAggregates();
            store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
            Map<String,Object> ok = baseResponse(ticket); ok.put("data", data); return ok;
        } catch (TimeoutException te) {
            combined.whenComplete(new BiConsumer<List<ResultItem>, Throwable>() {
                public void accept(List<ResultItem> val, Throwable err) {
                    ticket.status=Ticket.Status.SUCCESS; ticket.data=(err==null?val:java.util.Collections.emptyList()); ticket.progress=100; ticket.recomputeAggregates();
                    store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
                }
            });
            return baseResponse(ticket); // PROCESSING 快照
        } catch (ExecutionException e) {
            // 理论不会到（都有 fallback），兜底：有损成功、空结果
            ticket.status=Ticket.Status.SUCCESS; ticket.data=java.util.Collections.emptyList(); ticket.progress=100; ticket.recomputeAggregates();
            store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
            Map<String,Object> ok = baseResponse(ticket); ok.put("data", java.util.Collections.emptyList()); return ok;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ticket.status=Ticket.Status.SUCCESS; ticket.data=java.util.Collections.emptyList(); ticket.progress=100; ticket.recomputeAggregates();
            store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
            Map<String,Object> ok = baseResponse(ticket); ok.put("data", java.util.Collections.emptyList()); return ok;
        }
    }

    /* ====================== 全局软截止（到点统一 fallback，不失败） ====================== */
    private final ScheduledExecutorService softTimer = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        public Thread newThread(Runnable r){ Thread t=new Thread(r,"orchestrator-soft-ddl"); t.setDaemon(true); return t; }
    });

    public Map<String,Object> multiSmartAggregateSoftNoFail(final List<String> names, long waitMs, final long perTaskTimeoutMs, final long globalSoftMs) {

        final Ticket ticket = new Ticket(java.util.UUID.randomUUID().toString(), names, false);
        ticket.status = Ticket.Status.PROCESSING; store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);

        final List<CompletableFuture<String>> innerCfs = new ArrayList<CompletableFuture<String>>(names.size());
        final List<CompletableFuture<ResultItem>> resultCfs = new ArrayList<CompletableFuture<ResultItem>>(names.size());
        final AtomicBoolean finalized = new AtomicBoolean(false);

        for (final String name : names) {
            final Ticket.Subtask st = ticket.subtasks.get(name);
            st.status="RUNNING"; Ticket.advanceStage(st, Ticket.Stage.PREPARE); store.publishUpdate(ticket.taskId, ticket);

            Supplier<String> sup = new Supplier<String>() {
                public String get(){
                    Ticket.advanceStage(st, Ticket.Stage.CALL_DOWNSTREAM); store.publishUpdate(ticket.taskId, ticket);
                    String v = doBusiness(ticket.taskId, ticket, name, st);
                    Ticket.advanceStage(st, Ticket.Stage.MERGE); store.publishUpdate(ticket.taskId, ticket);
                    return v;
                }
            };
            final boolean[] degraded = new boolean[]{ false };

            CompletableFuture<String> inner = engine.supply(
                    sup, perTaskTimeoutMs, TimeUnit.MILLISECONDS,
                    new Supplier<String>() { public String get(){ degraded[0]=true; st.error="timeout/fallback"; return "fallback:"+name; } },
                    true // 子任务到点可中断
            );
            innerCfs.add(inner);

            CompletableFuture<ResultItem> result = new CompletableFuture<ResultItem>();
            inner.whenComplete(new BiConsumer<String, Throwable>() {
                public void accept(String val, Throwable err) {
                    ResultItem item;
                    if (err==null) {
                        if (degraded[0]) { st.status="FALLBACK"; item=ResultItem.fallback(name,val,st.error); }
                        else { st.status="SUCCESS"; item=ResultItem.success(name,val); }
                    } else {
                        st.status="FALLBACK"; st.error=shortErr(err); item=ResultItem.fallback(name,"fallback:"+name, st.error);
                    }
                    Ticket.advanceStage(st, Ticket.Stage.FINISH); st.endedAt=System.currentTimeMillis();
                    result.complete(item);
                    ticket.recomputeAggregates(); store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
                }
            });
            resultCfs.add(result);
        }

        final CompletableFuture<List<ResultItem>> combined =
                CompletableFuture.allOf(resultCfs.toArray(new CompletableFuture[0]))
                        .thenApply(new java.util.function.Function<Void, List<ResultItem>>() {
                            public List<ResultItem> apply(Void v){
                                List<ResultItem> out = new ArrayList<ResultItem>(resultCfs.size());
                                for (CompletableFuture<ResultItem> cf : resultCfs) out.add(cf.join());
                                return out;
                            }
                        });

        // 软截止：到点对未完成子任务直接填 fallback，并尽力取消真实执行
        softTimer.schedule(new Runnable() {
            public void run() {
                if (finalized.get()) return;
                if (!combined.isDone()) {
                    for (int i=0;i<resultCfs.size();i++) {
                        CompletableFuture<ResultItem> r = resultCfs.get(i);
                        if (!r.isDone()) {
                            String name = names.get(i);
                            Ticket.Subtask st = ticket.subtasks.get(name);
                            st.status="FALLBACK"; st.error="soft-deadline";
                            Ticket.advanceStage(st, Ticket.Stage.FINISH); st.endedAt=System.currentTimeMillis();
                            r.complete(ResultItem.fallback(name, "fallback:"+name, "soft-deadline"));
                            innerCfs.get(i).cancel(true);
                        }
                    }
                }
            }
        }, globalSoftMs, TimeUnit.MILLISECONDS);

        try {
            List<ResultItem> data = combined.get(waitMs, TimeUnit.MILLISECONDS);
            finalized.set(true);
            ticket.mode="SYNC"; ticket.status=Ticket.Status.SUCCESS; ticket.data=data; ticket.progress=100; ticket.recomputeAggregates();
            store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
            Map<String,Object> ok = baseResponse(ticket); ok.put("data", data); return ok;
        } catch (TimeoutException te) {
            combined.whenComplete(new BiConsumer<List<ResultItem>, Throwable>() {
                public void accept(List<ResultItem> val, Throwable err) {
                    if (finalized.get()) return;
                    finalized.set(true);
                    ticket.status=Ticket.Status.SUCCESS; ticket.data=(err==null?val:java.util.Collections.emptyList()); ticket.progress=100; ticket.recomputeAggregates();
                    store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
                }
            });
            return baseResponse(ticket);
        } catch (Exception e) {
            finalized.set(true);
            ticket.status=Ticket.Status.SUCCESS; ticket.data=java.util.Collections.emptyList(); ticket.progress=100; ticket.recomputeAggregates();
            store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
            Map<String,Object> ok = baseResponse(ticket); ok.put("data", java.util.Collections.emptyList()); return ok;
        }
    }

    /* ====================== Fail-Fast 严格聚合（任一失败整体失败） ====================== */
    public Map<String,Object> allFailFastAggregate(List<String> names, long waitMs, long perTaskTimeoutMs) {
        final Ticket ticket = new Ticket(java.util.UUID.randomUUID().toString(), names, true);
        ticket.status = Ticket.Status.PROCESSING; store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);

        final List<CompletableFuture<String>> innerCfs = new ArrayList<CompletableFuture<String>>(names.size());
        for (final String name : names) {
            final Ticket.Subtask st = ticket.subtasks.get(name);
            st.status="RUNNING"; Ticket.advanceStage(st, Ticket.Stage.PREPARE); store.publishUpdate(ticket.taskId, ticket);

            Supplier<String> sup = new Supplier<String>() {
                public String get(){
                    Ticket.advanceStage(st, Ticket.Stage.CALL_DOWNSTREAM); store.publishUpdate(ticket.taskId, ticket);
                    String v = doBusiness(ticket.taskId, ticket, name, st);
                    Ticket.advanceStage(st, Ticket.Stage.MERGE); store.publishUpdate(ticket.taskId, ticket);
                    return v;
                }
            };
            CompletableFuture<String> cf = engine.supplyFailFast(sup, perTaskTimeoutMs, TimeUnit.MILLISECONDS, true);
            innerCfs.add(cf);
            cf.whenComplete(new BiConsumer<String, Throwable>() {
                public void accept(String v, Throwable e) {
                    if (e == null) { st.status="SUCCESS"; Ticket.advanceStage(st, Ticket.Stage.FINISH); st.endedAt=System.currentTimeMillis(); }
                    else { st.status="FAILED"; st.error=shortErr(e); Ticket.advanceStage(st, Ticket.Stage.FINISH); st.endedAt=System.currentTimeMillis(); }
                    ticket.recomputeAggregates(); store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
                }
            });
        }

        CompletableFuture<List<String>> combined =
                engine.allFailFast(new ArrayList<Supplier<String>>() {{
                    for (final CompletableFuture<String> cf : innerCfs) add(new Supplier<String>() { public String get(){ return cf.join(); }});
                }}, waitMs, TimeUnit.MILLISECONDS, true);

        try {
            combined.get(waitMs, TimeUnit.MILLISECONDS);
            ticket.mode="SYNC"; ticket.status=Ticket.Status.SUCCESS; ticket.progress=100; ticket.recomputeAggregates();
            store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
            return baseResponse(ticket);
        } catch (TimeoutException te) {
            return baseResponse(ticket); // 仍在 PROCESSING
        } catch (Exception e) {
            ticket.mode="SYNC"; ticket.status=Ticket.Status.FAILED; ticket.error=shortErr(e); ticket.progress=100; ticket.recomputeAggregates();
            store.saveTicket(ticket); store.publishUpdate(ticket.taskId, ticket);
            return baseResponse(ticket);
        }
    }

    /* ====================== 业务模拟 / 工具 ====================== */

    /** 示例业务：请替换为你的真实逻辑（保留阶段推进与推送即可） */
    private String doBusiness(String taskId, Ticket ticket, String name, Ticket.Subtask st) {
        sleepCancellable(20); // 准备
        sleepCancellable(60 + Math.abs(name.hashCode() % 50)); // 下游调用
        if ( (name.hashCode() & 7) == 0 ) throw new RuntimeException("boom:" + name);
        sleepCancellable(20); // 合并/转换
        return "ok:" + name;
    }
    private static boolean sleepCancellable(long ms){
        try { Thread.sleep(ms); return true; }
        catch (InterruptedException e){ Thread.currentThread().interrupt(); return false; }
    }

    private static String shortErr(Throwable e){
        Throwable c = (e instanceof ExecutionException) ? e.getCause() : e;
        String msg = c==null ? "unknown" : String.valueOf(c.getMessage());
        return msg.length()>200?msg.substring(0,200):msg;
    }

    private Map<String,Object> baseResponse(Ticket t){
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("mode", t.mode);
        m.put("status", t.status.name());
        m.put("taskId", t.taskId);
        m.put("progress", t.progress);
        Map<String,Object> summary = new HashMap<String,Object>();
        summary.put("total", t.totalCount); summary.put("ok", t.okCount);
        summary.put("fallback", t.fallbackCount); summary.put("failed", t.failedCount);
        m.put("summary", summary);
        return m;
    }
}
