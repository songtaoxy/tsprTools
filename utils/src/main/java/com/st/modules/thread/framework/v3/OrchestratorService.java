package com.st.modules.thread.framework.v3;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 编排服务：
 * - allOf 聚合：单个失败填充 fallback
 * - anyOf 快速副本：先完成即返回
 * - 同步优先，超时 → 票据（taskId），后台完成可查询
 */


public  class OrchestratorService {

    private final Engine engine;
    private final Dispatcher dispatcher;
    private final TicketStore ticketStore;

    public OrchestratorService(Engine engine, Dispatcher dispatcher, TicketStore ticketStore) {
        this.engine = engine;
        this.dispatcher = dispatcher;
        this.ticketStore = ticketStore;
    }

    /** 并行聚合（按名称构建任务，仅示例） */
    public List<String> aggregate(List<String> names, long globalTimeoutMs) {
        Deadline ddl = Deadline.after(globalTimeoutMs, TimeUnit.MILLISECONDS);
        List<Supplier<String>> tasks = new ArrayList<Supplier<String>>(names.size());
        for (String n : names) {
            long step = Engine.stepTimeoutMs(ddl, 200); // 每步兜底 200ms
            tasks.add(() -> simulateIO("A:" + n, step));
        }
        try {
            return engine.allWithFallback(tasks, ex -> "fallback", globalTimeoutMs, TimeUnit.MILLISECONDS).get();
        } catch (Exception e) {
            return Collections.<String>emptyList();
        }
    }

    /** 快速副本（anyOf） */
    public String fastReplica(String key, long timeoutMs) {
        List<Supplier<String>> replicas = Arrays.<Supplier<String>>asList(
                () -> simulateIO("az1:" + key, 120),
                () -> simulateIO("az2:" + key, 150)
        );
        try { return engine.anyOfOrFallback(replicas, "fallback", timeoutMs, TimeUnit.MILLISECONDS).get(); }
        catch (Exception e) { return "fallback"; }
    }

    /** 同步优先，超时转票据 */
    public Map<String, Object> submitWithTicket(String name, long waitMs) {
        ObjectNode meta = JsonNodeFactory.instance.objectNode();
        AsyncTaskContext ctx = new AsyncTaskContext(meta);
        String taskId = ctx.taskId();

        CompletableFuture<String> promise = new CompletableFuture<String>();
        dispatcher.dispatchWithRetry(ctx, () -> simulateIO("job:" + name, 150),
                new Callback<String>() {
                    public void onSuccess(AsyncTaskContext c, String result) {
                        c.success(result); ticketStore.save(taskId, result); promise.complete(result);
                    }
                    public void onFailure(AsyncTaskContext c, Throwable ex) {
                        c.fail(ex); ticketStore.save(taskId, "fallback"); promise.complete("fallback");
                    }
                },
                2, Backoff.expoJitter(50, 200, TimeUnit.MILLISECONDS)
        );

        try {
            String v = promise.get(waitMs, TimeUnit.MILLISECONDS);
            Map<String,Object> ok = new HashMap<String,Object>();
            ok.put("taskId", taskId); ok.put("status","SUCCESS"); ok.put("data", v);
            return ok;
        } catch (TimeoutException te) {
            Map<String,Object> ticket = new HashMap<String,Object>();
            ticket.put("taskId", taskId); ticket.put("status","PROCESSING");
            return ticket;
        } catch (Exception e) {
            Map<String,Object> err = new HashMap<String,Object>();
            err.put("taskId", taskId); err.put("status","FAILED");
            return err;
        }
    }


    /**
     * <pre>
     *     - 场景: 多任务并发执行 → Controller 只等 waitMs；
     *     能等到就同步返回；等不到就立刻返回票据。
     *     后台继续跑到“全局软截止时间（soft deadline）”，
     *     到点还没完成的子任务一律用 fallback 收尾，整单不标记失败
     * </pre>
     *
     * <pre>
     *     具体说明:
     *     - 每个子任务都有自己的超时 + fallback（有损）；
     *     - 还设置一个全局软截止：到点不失败，而是把未完成的子任务直接置为 fallback，并完成整单；
     *     - Controller 同步窗口 waitMs 内没拿到结果 ⇒ 立即返回票据，后台按上面规则收尾；
     *     - 进度/失败明细通过 Ticket + SSE（沿用你前面已经接的 TicketStore/Redis 版）。
     * </pre>
     */
    // OrchestratorService 内新增
    private final java.util.concurrent.ScheduledExecutorService softTimer =
            java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "orchestrator-soft-ddl"); t.setDaemon(true); return t;
            });

    /**
     * 多任务并发：同步窗口内尽量返回；否则票据化；全局“软超时”不失败，未完成子任务统一 fallback 收尾。
     *
     * @param names         子任务标识（示例）
     * @param waitMs        同步等待窗口（如 120）
     * @param perTaskTimeoutMs  每个子任务自己的超时（如 150），到点走 fallback
     * @param globalSoftMs  全局软截止（如 400），到点将未完成子任务置为 fallback 并整体成功
     */
    public java.util.Map<String,Object> multiSmartAggregateSoftNoFail(
            java.util.List<String> names,
            long waitMs,
            long perTaskTimeoutMs,
            long globalSoftMs) {

        // 1) 建票据（含进度/失败明细）
        final Ticket ticket = new Ticket(java.util.UUID.randomUUID().toString(), names, false);
        ticket.status = Ticket.Status.PROCESSING; ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);

        final int n = names.size();
        final java.util.List<java.util.concurrent.CompletableFuture<String>> innerCfs = new java.util.ArrayList<>(n); // 真实执行
        final java.util.List<java.util.concurrent.CompletableFuture<String>> resultCfs = new java.util.ArrayList<>(n); // 可外部强制填充fallback
        final java.util.concurrent.atomic.AtomicBoolean finalized = new java.util.concurrent.atomic.AtomicBoolean(false);

        // 2) 启动每个子任务（有损：超时→fallback；成功/降级都算“完成”）
        for (String name : names) {
            final Ticket.Subtask st = ticket.subtasks.get(name);
            st.status = "RUNNING"; st.progress = 10; Ticket.advanceStage(st, Ticket.Stage.PREPARE);
            ticketStore.publishUpdate(ticket.taskId, ticket);

            // 真实业务调用（你可以替换 doBusiness）
            java.util.function.Supplier<String> sup = () -> {
                Ticket.advanceStage(st, Ticket.Stage.CALL_DOWNSTREAM); ticketStore.publishUpdate(ticket.taskId, ticket);
                String v = doBusiness_simple(name, st); // 可抛异常
                Ticket.advanceStage(st, Ticket.Stage.MERGE); ticketStore.publishUpdate(ticket.taskId, ticket);
                return v;
            };

            // 真实执行 CF：到点走 fallback；不失败
            final boolean[] degradedFlag = { false };
            java.util.concurrent.CompletableFuture<String> inner = engine.supply(
                    sup,
                    perTaskTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS,
                    () -> { degradedFlag[0] = true; st.error = "fallback"; return "fallback:" + name; },
                    true // per-task 到点可中断
            );

            // 对外“可控”的结果 CF：把 inner 填进来；若软截止到点，还没完成就我们手工 complete 为 fallback
            java.util.concurrent.CompletableFuture<String> result = new java.util.concurrent.CompletableFuture<>();
            inner.whenComplete((val, err) -> {
                if (err == null) {
                    if (degradedFlag[0]) { st.status = "FALLBACK"; } else { st.status = "SUCCESS"; }
                    Ticket.advanceStage(st, Ticket.Stage.FINISH); st.endedAt = System.currentTimeMillis();
                    result.complete(val);
                } else {
                    // 理论上不会走到（engine.supply 已用 fallback 吞掉错误），兜底处理
                    st.status = "FALLBACK"; st.error = shortErr(err); Ticket.advanceStage(st, Ticket.Stage.FINISH);
                    result.complete("fallback:" + name);
                }

                ticket.recomputeAggregates();
                ticketStore.publishUpdate(ticket.taskId, ticket);
            });

            innerCfs.add(inner); resultCfs.add(result);
        }

        // 3) 聚合所有“可控结果”CF
        java.util.concurrent.CompletableFuture<java.util.List<String>> combined =
                java.util.concurrent.CompletableFuture.allOf(resultCfs.toArray(new java.util.concurrent.CompletableFuture[0]))
                        .thenApply(v -> {
                            java.util.List<String> out = new java.util.ArrayList<>(resultCfs.size());
                            for (java.util.concurrent.CompletableFuture<String> cf : resultCfs) out.add(cf.join());
                            return out;
                        });

        // 4) 设置“全局软截止” —— 到点不失败；对未完成子任务强制回填 fallback 并结束整单
        softTimer.schedule(() -> {
            if (finalized.get()) return;
            if (!combined.isDone()) {
                // 强制将未完成子任务填充为 fallback（并尽力取消真实执行）
                for (int i = 0; i < resultCfs.size(); i++) {
                    java.util.concurrent.CompletableFuture<String> r = resultCfs.get(i);
                    if (!r.isDone()) {
                        String name = names.get(i);
                        Ticket.Subtask st = ticket.subtasks.get(name);
                        st.status = "FALLBACK"; st.error = "soft-deadline"; Ticket.advanceStage(st, Ticket.Stage.FINISH);
                        r.complete("fallback:" + name);
                        // 尝试取消底层真实执行（注意 engine.supply 的取消不一定能传到底层，但无伤大雅）
                        innerCfs.get(i).cancel(true);
                    }
                }
            }
        }, globalSoftMs, java.util.concurrent.TimeUnit.MILLISECONDS);

        // 5) 同步窗口内尝试返回；否则票据化
        try {
            java.util.List<String> data = combined.get(waitMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            // 同步成功
            finalized.set(true);
            ticket.mode = "SYNC"; ticket.status = Ticket.Status.SUCCESS; ticket.data = data; ticket.progress = 100; ticket.touch();
            ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);
            java.util.Map<String,Object> ok = new java.util.HashMap<>();
            ok.put("mode","SYNC"); ok.put("status","SUCCESS"); ok.put("taskId", ticket.taskId); ok.put("data", data); ok.put("progress", 100);
            return ok;
        } catch (java.util.concurrent.TimeoutException te) {
            // 进入异步：等待“自然完成或软截止收尾”，最终都标记 SUCCESS（有损）
            combined.whenComplete((val, err) -> {
                if (finalized.get()) return;
                finalized.set(true);
                ticket.status = Ticket.Status.SUCCESS; // 软截止也算成功（有损）
                ticket.data = (err == null ? val : java.util.Collections.emptyList());
                ticket.progress = 100; ticket.touch();
                ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);
            });
            java.util.Map<String,Object> ticketRes = new java.util.HashMap<>();
            ticketRes.put("mode","ASYNC"); ticketRes.put("taskId", ticket.taskId);
            ticketRes.put("status","PROCESSING"); ticketRes.put("progress", ticket.progress);
            return ticketRes;
        } catch (java.util.concurrent.ExecutionException ee) {
            // 理论上不会：所有子任务都有 fallback；兜底（仍不失败，可按需改成 5xx）
            finalized.set(true);
            ticket.status = Ticket.Status.SUCCESS; ticket.data = java.util.Collections.emptyList();
            ticket.progress = 100; ticket.touch(); ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);
            java.util.Map<String,Object> ok = new java.util.HashMap<>();
            ok.put("mode","SYNC"); ok.put("status","SUCCESS"); ok.put("taskId", ticket.taskId); ok.put("data", java.util.Collections.emptyList()); ok.put("progress", 100);
            return ok;
        } catch (java.lang.InterruptedException ie) {
            Thread.currentThread().interrupt();
            // 中断也做有损成功处理（按需可返回票据）
            finalized.set(true);
            ticket.status = Ticket.Status.SUCCESS; ticket.data = java.util.Collections.emptyList();
            ticket.progress = 100; ticket.touch(); ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);
            java.util.Map<String,Object> ok = new java.util.HashMap<>();
            ok.put("mode","SYNC"); ok.put("status","SUCCESS"); ok.put("taskId", ticket.taskId); ok.put("data", java.util.Collections.emptyList()); ok.put("progress", 100);
            return ok;
        }
    }

    /* 简短的错误摘要 */
    private String shortErr(Throwable e) {
        Throwable c = (e instanceof java.util.concurrent.ExecutionException) ? e.getCause() : e;
        String msg = c == null ? "unknown" : String.valueOf(c.getMessage());
        return msg.length() > 200 ? msg.substring(0,200) : msg;
    }


    // OrchestratorService 内新增方法
    public Map<String,Object> multiSmartAggregateOpenEnded(
            List<String> names,
            long waitMs,
            Long perTaskTimeoutMs // 为 null 表示不设子任务超时
    ) {
        // 1) 建票据
        final Ticket ticket = new Ticket(java.util.UUID.randomUUID().toString(), names, false);
        ticket.status = Ticket.Status.PROCESSING;
        ticketStore.saveTicket(ticket);
        ticketStore.publishUpdate(ticket.taskId, ticket);

        final int n = names.size();
        final List<CompletableFuture<String>> innerCfs  = new ArrayList<>(n); // 真实执行
        final List<CompletableFuture<String>> resultCfs = new ArrayList<>(n); // 汇总（可填充 fallback）

        // 2) 启动每个子任务：失败/自身超时 => fallback；其余不受“全局时限”限制
        for (String name : names) {
            final Ticket.Subtask st = ticket.subtasks.get(name);
            st.status = "RUNNING"; Ticket.advanceStage(st, Ticket.Stage.PREPARE);
            ticketStore.publishUpdate(ticket.taskId, ticket);

            // 你的实际业务调用（可抛异常）
            java.util.function.Supplier<String> sup = () -> {
                Ticket.advanceStage(st, Ticket.Stage.CALL_DOWNSTREAM);
                String v = doBusiness_simple(name, st);                 // TODO: 替换为真实逻辑
                Ticket.advanceStage(st, Ticket.Stage.MERGE);
                return v;
            };

            final boolean[] degraded = { false };
            long stepTimeout = (perTaskTimeoutMs == null ? Long.MAX_VALUE : perTaskTimeoutMs);

            // 子任务级治理：发生异常/到自身超时 => fallback；否则返回真实值
            CompletableFuture<String> inner = engine.supply(
                    sup,
                    stepTimeout, TimeUnit.MILLISECONDS,
                    () -> { degraded[0] = true; st.error = "fallback"; return "fallback:" + name; },
                    true // 子任务到点可中断
            );

            // 对外的可控 future：把 inner 完成结果灌入，并维护票据进度与状态
            CompletableFuture<String> result = new CompletableFuture<>();
            inner.whenComplete((val, err) -> {
                if (err == null) {
                    st.status = degraded[0] ? "FALLBACK" : "SUCCESS";
                    Ticket.advanceStage(st, Ticket.Stage.FINISH);
                    st.endedAt = System.currentTimeMillis();
                    result.complete(val);
                } else {
                    // 理论上不会走到（engine.supply 已用 fallback 吞错），兜底
                    st.status = "FALLBACK"; st.error = shortErr(err);
                    Ticket.advanceStage(st, Ticket.Stage.FINISH);
                    st.endedAt = System.currentTimeMillis();
                    result.complete("fallback:" + name);
                }
                // 任务每次状态或阶段变更后：
                ticket.recomputeAggregates();
                ticketStore.saveTicket(ticket);
                ticketStore.publishUpdate(ticket.taskId, ticket);

            });

            innerCfs.add(inner); resultCfs.add(result);
        }

        // 3) 汇总完成：所有子任务（真实或 fallback）完成即得到最终结果
        CompletableFuture<List<String>> combined =
                CompletableFuture.allOf(resultCfs.toArray(new CompletableFuture[0]))
                        .thenApply(v -> {
                            List<String> out = new ArrayList<>(resultCfs.size());
                            for (CompletableFuture<String> cf : resultCfs) out.add(cf.join());
                            return out;
                        });

        // 4) 同步窗口：能等到就同步返回；等不到就票据化（后台继续执行直到全部结束）
        try {
            List<String> data = combined.get(waitMs, TimeUnit.MILLISECONDS);
            ticket.mode = "SYNC"; ticket.status = Ticket.Status.SUCCESS; ticket.data = data;
            ticket.progress = 100; ticket.touch();
            ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);
            Map<String,Object> ok = new HashMap<>();
            ok.put("mode","SYNC"); ok.put("status","SUCCESS"); ok.put("taskId", ticket.taskId);
            ok.put("data", data); ok.put("progress", 100);
            return ok;
        } catch (TimeoutException te) {
            // 异步返回票据；后台“无全局时限”，继续跑到全部结束
            combined.whenComplete((val, err) -> {
                ticket.status = Ticket.Status.SUCCESS; // 有损成功：失败项用 FALLBACK 占位
                ticket.data = (err == null ? val : java.util.Collections.emptyList());
                ticket.progress = 100; ticket.touch();
                ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);
            });
            Map<String,Object> ticketRes = new HashMap<>();
            ticketRes.put("mode","ASYNC"); ticketRes.put("taskId", ticket.taskId);
            ticketRes.put("status","PROCESSING"); ticketRes.put("progress", ticket.progress);
            return ticketRes;
        } catch (ExecutionException ee) {
            // 理论上不会：每个子任务都有 fallback。兜底仍按有损成功处理
            ticket.status = Ticket.Status.SUCCESS; ticket.data = java.util.Collections.emptyList();
            ticket.progress = 100; ticket.touch();
            ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);
            Map<String,Object> ok = new HashMap<>();
            ok.put("mode","SYNC"); ok.put("status","SUCCESS"); ok.put("taskId", ticket.taskId);
            ok.put("data", java.util.Collections.emptyList()); ok.put("progress", 100);
            return ok;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ticket.status = Ticket.Status.SUCCESS; ticket.data = java.util.Collections.emptyList();
            ticket.progress = 100; ticket.touch();
            ticketStore.saveTicket(ticket); ticketStore.publishUpdate(ticket.taskId, ticket);
            Map<String,Object> ok = new HashMap<>();
            ok.put("mode","SYNC"); ok.put("status","SUCCESS"); ok.put("taskId", ticket.taskId);
            ok.put("data", java.util.Collections.emptyList()); ok.put("progress", 100);
            return ok;
        }
    }


    public Object queryTicket(String taskId) {
        Object v = ticketStore.get(taskId);
        return v == null ? "PROCESSING" : v;
    }

    /* ----------- 示例 I/O ------------ */
    public  String simulateIO(String tag, long ms) {
        try { Thread.sleep(Math.max(1, ms)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        if ((tag.hashCode() & 7) == 0) throw new RuntimeException("boom"); // 偶发错误，演示重试/回退
        return tag;
    }


    private String doBusiness_simple(String name, Ticket.Subtask st) {
        // 你的真实业务调用；下面是演示——随机失败/耗时
        st.progress = 50;
        try { Thread.sleep(80 + Math.abs(name.hashCode() % 60)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new RuntimeException("cancelled", e); }
        if ((name.hashCode() & 7) == 0) throw new RuntimeException("boom:" + name);
        return "ok:" + name;
    }

    /**
     * <pre>
     *     设计: 可查询 & 可更新 context / Ticket
     *     - 有 阶段推进（PREPARE/CALL_DOWNSTREAM/MERGE/FINISH）；
     *     - 会 更新 Ticket.Subtask 并通过 store.publishUpdate(taskId, ticket) 触发 SSE；
     *     - 对齐你“子线程在不同业务阶段查询/更新 context”的诉求。
     *     - 如果你已经接了 BizContext，也可以把 taskId 与 ticket 放进 BizContext（如 ctx.put("ticket", ticket)），在方法里 BizContextHolder.get() 取出，就不必传参；两种都可以
     * </pre>
     *
     * 使用
     * <pre>
     *     {@code
     *     final String taskId = ticket.taskId;          // ticket 为方法内 final 变量
     * Supplier<String> sup = () -> doBusiness(taskId, ticket, name, st);
     *
     * // 子任务有超时就用 fallback，占位；没有全局硬超时
     * CompletableFuture<String> inner = engine.supply(
     *     sup,
     *     stepTimeout, TimeUnit.MILLISECONDS,
     *     () -> { st.status = "FALLBACK"; st.error = "timeout/fallback"; Ticket.advanceStage(st, Ticket.Stage.FINISH); st.endedAt=System.currentTimeMillis(); store.publishUpdate(taskId, ticket); return "fallback:" + name; },
     *     true
     * );
     *     }
     * </pre>
     *
     * <pre>
     *     - 改进: 把 ResultItem（结构化输出）也在 doBusiness 里直接生成，
     *       并在 fallback 分支保持同结构返回，减少转换代码
     * </pre>
     * @param taskId
     * @param ticket
     * @param name
     * @param st
     * @return
     */
    public String doBusiness(String taskId, Ticket ticket, String name, Ticket.Subtask st) {
        // 进入任务：标记阶段 & 推送
        st.status = "RUNNING";
        Ticket.advanceStage(st, Ticket.Stage.PREPARE);
        ticketStore.publishUpdate(taskId, ticket);

        // 调用下游前准备
        sleepOk(20);

        // 调下游（这里用模拟耗时/随机异常；替换成你的真实调用）
        Ticket.advanceStage(st, Ticket.Stage.CALL_DOWNSTREAM);
        ticketStore.publishUpdate(taskId, ticket);

        sleepOk(60 + Math.abs(name.hashCode() % 50));
        if ((name.hashCode() & 7) == 0) {
            // 让上层 Engine.supply 捕获异常并走 fallback
            throw new RuntimeException("boom:" + name);
        }

        // 合并/转换
        Ticket.advanceStage(st, Ticket.Stage.MERGE);
        ticketStore.publishUpdate(taskId, ticket);
        sleepOk(20);

        // 正常完成
        Ticket.advanceStage(st, Ticket.Stage.FINISH);
        st.status = "SUCCESS";
        st.endedAt = System.currentTimeMillis();
        ticketStore.publishUpdate(taskId, ticket);

        return "ok:" + name;
    }

    public void sleepOk(long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); throw new RuntimeException("cancelled", e);
        }
    }



   /* private String doBusiness_simple_v2(String name, Ticket.Subtask st) {
        st.status = "RUNNING";
        Ticket.advanceStage(st, Ticket.Stage.PREPARE);
        ticketStore.publishUpdate(currentTaskId(), currentTicket());

        // ↓↓↓ 调用下游前的准备
        sleepOk(20);

        Ticket.advanceStage(st, Ticket.Stage.CALL_DOWNSTREAM);
        ticketStore.publishUpdate(currentTaskId(), currentTicket());
        // ↓↓↓ 调下游（可抛瞬时错误触发 Fail-Fast 或 fallback）
        sleepOk(60 + Math.abs(name.hashCode() % 50));
        if ((name.hashCode() & 7) == 0) throw new RuntimeException("boom:" + name);

        Ticket.advanceStage(st, Ticket.Stage.MERGE);
        ticketStore.publishUpdate(currentTaskId(), currentTicket());
        // ↓↓↓ 合并/转换
        sleepOk(20);

        Ticket.advanceStage(st, Ticket.Stage.FINISH);
        st.status = "SUCCESS"; st.endedAt = System.currentTimeMillis();
        ticketStore.publishUpdate(currentTaskId(), currentTicket());
        return "ok:" + name;*/


    /*    private void updateOverallProgress(Ticket t) {
        int sum = 0;
        for (Ticket.Subtask s : t.subtasks.values()) sum += s.progress;
        t.progress = Math.min(100, sum / t.total());
        t.touch();
    }

    private void updateOverallProgress(Ticket t) {
        int done = t.doneCount();
        int total = Math.max(1, t.total());
        t.progress = Math.min(100, (int) Math.floor(done * 100.0 / total));
        t.touch();
    }*/

}


