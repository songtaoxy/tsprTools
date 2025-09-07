package com.st.modules.thread.framework.v4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 并发聚合编排服务（骨架版）：
 * - 开放式有损聚合（窗口内同步，否则票据化）
 * - 软截止不失败（到点强制收尾，整体成功）
 * - 严格聚合（Fail-Fast）
 *
 * 说明：
 * 1) 不做任何“旧版本”的兼容，围绕当前 Ticket / Engine / BizContext 设计。
 * 2) 票据与进度通过 TicketStore 即时推送（SSE/查询）。
 * 3) 所有收尾路径通过 finalizeOnce(...) 保证幂等。
 */
public class OrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(OrchestratorService.class);

    private final Engine engine;
    private final TicketStore store;
    private final ScheduledExecutorService scheduler;

    /** 注入 Engine 与 TicketStore；scheduler 可外部提供，默认单线程守护调度器 */
    public OrchestratorService(Engine engine, TicketStore store) {
        this(engine, store, Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "orch-scheduler");
            t.setDaemon(true);
            return t;
        }));
    }

    public OrchestratorService(Engine engine, TicketStore store, ScheduledExecutorService scheduler) {
        this.engine = engine;
        this.store = store;
        this.scheduler = scheduler;
    }

    // =====================================================
    // ==============  公开编排接口（3个）  =================
    // =====================================================

    /**
     * 开放式有损聚合：
     * - 等待窗口 waitMs 内全部完成 → SYNC 返回
     * - 否则 → 返回 ASYNC 票据快照，后台继续直到自然结束
     * - perTaskTimeoutMs == null → 子任务无步超时；否则到点按 fallback 占位（不打断线程）
     */
    public Map<String, Object> multiSmartAggregateOpenEnded(List<String> names, long waitMs, Long perTaskTimeoutMs) {
        Objects.requireNonNull(names, "names");
        final String taskId = UUID.randomUUID().toString();
        final Ticket ticket = new Ticket(taskId, names, /*strictAll*/ false);
        ticket.status = Ticket.Status.PROCESSING;
        ticket.mode   = Ticket.Mode.ASYNC; // 首帧先认为是异步
        store.saveTicket(ticket);
        store.publishUpdate(taskId, ticket);

        final AtomicBoolean finalized = new AtomicBoolean(false);

        // 为每个子任务创建 Future（带步超时与 fallback）
        final List<CompletableFuture<ResultItem>> tasks = names.stream()
                .map(n -> spawnLossyTask(taskId, ticket, n, perTaskTimeoutMs))
                .collect(Collectors.toList());

        final CompletableFuture<List<ResultItem>> combined = allOfList(tasks);

        // 后台完成 → 幂等收尾（ASYNC/SUCCESS）
        combined.whenComplete((data, ex) -> {
            if (ex == null) {
                finalizeOnce(finalized, ticket, () -> {
                    ticket.mode = Ticket.Mode.ASYNC;
                    ticket.status = Ticket.Status.SUCCESS;
                    ticket.data.clear();
                    ticket.data.put("items", data);
                });
            } else {
                // 理论少见（任务都有 fallback），兜底：有损成功但空列表
                finalizeOnce(finalized, ticket, () -> {
                    ticket.mode = Ticket.Mode.ASYNC;
                    ticket.status = Ticket.Status.SUCCESS;
                    ticket.data.clear();
                    ticket.data.put("items", Collections.emptyList());
                    ticket.error = null;
                });
            }
        });

        // 尝试在等待窗口内同步返回
        try {
            List<ResultItem> data = combined.get(waitMs, TimeUnit.MILLISECONDS);
            boolean first = finalizeOnce(finalized, ticket, () -> {
                ticket.mode = Ticket.Mode.SYNC;
                ticket.status = Ticket.Status.SUCCESS;
                ticket.data.clear();
                ticket.data.put("items", data);
            });
            Map<String, Object> ok = baseResponse(ticket);
            ok.put("data", first ? data : snapshotItems(ticket));
            return ok;
        } catch (TimeoutException te) {
            // 返回进行中快照（后台继续）
            Map<String, Object> processing = baseResponse(ticket);
            processing.put("status", Ticket.Status.PROCESSING.name());
            return processing;
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            // 理论不常见：兜底为有损成功 + 空结果
            finalizeOnce(finalized, ticket, () -> {
                ticket.mode = Ticket.Mode.SYNC;
                ticket.status = Ticket.Status.SUCCESS;
                ticket.data.clear();
                ticket.data.put("items", Collections.emptyList());
            });
            Map<String,Object> ok = baseResponse(ticket);
            ok.put("data", Collections.emptyList());
            return ok;
        }
    }

    /**
     * 软截止不失败：
     * - 到 globalSoftMs 时：将未完成子任务标记 fallback 并尝试 cancel(true)
     * - 整单 SUCCESS；可在窗口内同步返回，否则 ASYNC 票据，后台很快会由软截止收尾
     */
    public Map<String, Object> multiSmartAggregateSoftNoFail(List<String> names, long waitMs, long perTaskTimeoutMs, long globalSoftMs) {
        Objects.requireNonNull(names, "names");
        final String taskId = UUID.randomUUID().toString();
        final Ticket ticket = new Ticket(taskId, names, /*strictAll*/ false);
        ticket.status = Ticket.Status.PROCESSING;
        ticket.mode   = Ticket.Mode.ASYNC;
        store.saveTicket(ticket);
        store.publishUpdate(taskId, ticket);

        final AtomicBoolean finalized = new AtomicBoolean(false);

        // 子任务（带步超时与 fallback）
        final Map<String, CompletableFuture<ResultItem>> futures = new HashMap<>();
        for (String n : names) {
            futures.put(n, spawnLossyTask(taskId, ticket, n, perTaskTimeoutMs));
        }
        final CompletableFuture<List<ResultItem>> combined = allOfList(new ArrayList<>(futures.values()));

        // 软截止定时器：到点把未完成子任务标记 fallback，并尝试中断
        final ScheduledFuture<?> soft = scheduler.schedule(() -> {
            // 标记未完成子任务为 fallback（可选：尝试 cancel(true) 快速回收）
            for (Map.Entry<String, CompletableFuture<ResultItem>> e : futures.entrySet()) {
                if (!e.getValue().isDone()) {
                    Ticket.Subtask st = ticket.subtasks.get(e.getKey());
                    if (st != null && !"FALLBACK".equals(st.status) && !st.isDone()) {
                        Ticket.advanceStage(st, Ticket.Stage.FINISH);
                        st.status = "FALLBACK";
                        st.error  = "soft-deadline";
                        st.progress.set(100);
                    }
                    e.getValue().cancel(true); // 允许中断（引擎/任务内应正确处理 CancellationException）
                }
            }
            store.publishUpdate(taskId, ticket);

            // 软截止统一收尾（ASYNC/SUCCESS，有损）
            finalizeOnce(finalized, ticket, () -> {
                ticket.mode = Ticket.Mode.ASYNC;
                ticket.status = Ticket.Status.SUCCESS;
                // 收集当前已知结果（可能部分仍在 fallback）
                ticket.data.put("items", snapshotItems(ticket));
            });
        }, Math.max(1, globalSoftMs), TimeUnit.MILLISECONDS);

        // 后台自然完成（若早于软截止）
        combined.whenComplete((data, ex) -> {
            if (!soft.isDone()) soft.cancel(false);
            if (ex == null) {
                finalizeOnce(finalized, ticket, () -> {
                    ticket.mode = Ticket.Mode.ASYNC;
                    ticket.status = Ticket.Status.SUCCESS;
                    ticket.data.clear();
                    ticket.data.put("items", data);
                });
            } else {
                finalizeOnce(finalized, ticket, () -> {
                    ticket.mode = Ticket.Mode.ASYNC;
                    ticket.status = Ticket.Status.SUCCESS;
                    ticket.data.put("items", snapshotItems(ticket));
                });
            }
        });

        // 窗口内尝试同步返回
        try {
            List<ResultItem> data = combined.get(waitMs, TimeUnit.MILLISECONDS);
            boolean first = finalizeOnce(finalized, ticket, () -> {
                ticket.mode = Ticket.Mode.SYNC;
                ticket.status = Ticket.Status.SUCCESS;
                ticket.data.clear();
                ticket.data.put("items", data);
            });
            Map<String, Object> ok = baseResponse(ticket);
            ok.put("data", first ? data : snapshotItems(ticket));
            return ok;
        } catch (TimeoutException te) {
            Map<String, Object> processing = baseResponse(ticket);
            processing.put("status", Ticket.Status.PROCESSING.name());
            return processing;
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            finalizeOnce(finalized, ticket, () -> {
                ticket.mode = Ticket.Mode.SYNC;
                ticket.status = Ticket.Status.SUCCESS;
                ticket.data.clear();
                ticket.data.put("items", snapshotItems(ticket));
            });
            Map<String,Object> ok = baseResponse(ticket);
            ok.put("data", snapshotItems(ticket));
            return ok;
        }
    }

    /**
     * 严格聚合（Fail-Fast）：
     * - 任一子任务异常 → 整体 FAILED（可选取消在途）
     * - 全部成功 → SUCCESS，同步返回
     */
    public Map<String, Object> allFailFastAggregate(List<String> names, long waitMs, long perTaskTimeoutMs) {
        Objects.requireNonNull(names, "names");
        final String taskId = UUID.randomUUID().toString();
        final Ticket ticket = new Ticket(taskId, names, /*strictAll*/ true);
        ticket.status = Ticket.Status.PROCESSING;
        ticket.mode   = Ticket.Mode.ASYNC;
        store.saveTicket(ticket);
        store.publishUpdate(taskId, ticket);

        // Fail-Fast：使用引擎提供的“严格”执行（异常直接抛出），否则自行聚合后检查
        final List<CompletableFuture<ResultItem>> futures = new ArrayList<>();
        for (String n : names) {
            futures.add(spawnStrictTask(taskId, ticket, n, perTaskTimeoutMs));
        }

        // 等待窗口：严格场景通常能够一次拿齐，若没拿齐仍同步等到全部（也可按需改为票据化）
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(waitMs, TimeUnit.MILLISECONDS);

            // 有任何 FAILED？
            boolean anyFailed = ticket.subtasks.values().stream().anyMatch(st -> "FAILED".equals(st.status));
            if (anyFailed) {
                ticket.mode   = Ticket.Mode.SYNC;
                ticket.status = Ticket.Status.FAILED;
                ticket.recomputeAggregates();
                store.saveTicket(ticket);
                store.publishUpdate(taskId, ticket);
            } else {
                List<ResultItem> data = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
                ticket.mode   = Ticket.Mode.SYNC;
                ticket.status = Ticket.Status.SUCCESS;
                ticket.data.clear();
                ticket.data.put("items", data);
                ticket.recomputeAggregates();
                store.saveTicket(ticket);
                store.publishUpdate(taskId, ticket);
            }
            return baseResponse(ticket);
        } catch (TimeoutException te) {
            // 如需：可改为票据化；这里保持简单，标记失败（或延长 waitMs）
            ticket.mode   = Ticket.Mode.SYNC;
            ticket.status = Ticket.Status.FAILED;
            ticket.error  = "timeout";
            ticket.recomputeAggregates();
            store.saveTicket(ticket);
            store.publishUpdate(taskId, ticket);
            return baseResponse(ticket);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            ticket.mode   = Ticket.Mode.SYNC;
            ticket.status = Ticket.Status.FAILED;
            ticket.error  = e.getMessage();
            ticket.recomputeAggregates();
            store.saveTicket(ticket);
            store.publishUpdate(taskId, ticket);
            return baseResponse(ticket);
        }
    }

    // =====================================================
    // ==================  任务生成器  =====================
    // =====================================================

    /** 有损子任务：允许超时降级（fallback），异常也降级；默认不打断线程 */
    private CompletableFuture<ResultItem> spawnLossyTask(String taskId, Ticket ticket, String name, Long perTaskTimeoutMs) {
        final Ticket.Subtask st = ticket.subtasks.get(name);
        st.status = "RUNNING";
        st.startedAt = System.currentTimeMillis();
        st.progress.set(0);
        ensureMeta(st);
        store.publishUpdate(taskId, ticket);

        Supplier<String> biz = () -> doBusiness(taskId, ticket, name, st);
        Supplier<String> fallback = () -> {
            st.status = "FALLBACK";
            st.error = "timeout/fallback";
            st.endedAt = System.currentTimeMillis();
            Ticket.advanceStage(st, Ticket.Stage.FINISH);
            store.publishUpdate(taskId, ticket);
            return "fallback:" + name;
        };

        CompletableFuture<String> inner;
        if (perTaskTimeoutMs == null) {
            // 若 Engine 无 supplyNoTimeout，可改为：engine.supply(biz, Long.MAX_VALUE, TimeUnit.DAYS, fallback, false)
            inner = engine.supplyNoTimeout(biz, fallback);
        } else {
            inner = engine.supply(biz, perTaskTimeoutMs, TimeUnit.MILLISECONDS, fallback, false);
        }

        return inner.handle((val, ex) -> {
            if (ex != null) {
                st.status = "FALLBACK";
                st.error = rootMsg(ex);
                st.endedAt = System.currentTimeMillis();
                Ticket.advanceStage(st, Ticket.Stage.FINISH);
                store.publishUpdate(taskId, ticket);
                return new ResultItem(name, null, "FALLBACK", st.error);
            } else {
                st.status = "SUCCESS";
                st.endedAt = System.currentTimeMillis();
                Ticket.advanceStage(st, Ticket.Stage.FINISH);
                store.publishUpdate(taskId, ticket);
                return new ResultItem(name, val, "SUCCESS", null);
            }
        });
    }

    /** 严格子任务：异常直接标记 FAILED 并上抛（Fail-Fast） */
    private CompletableFuture<ResultItem> spawnStrictTask(String taskId, Ticket ticket, String name, long perTaskTimeoutMs) {
        final Ticket.Subtask st = ticket.subtasks.get(name);
        st.status = "RUNNING";
        st.startedAt = System.currentTimeMillis();
        st.progress.set(0);
        ensureMeta(st);
        store.publishUpdate(taskId, ticket);

        Supplier<String> biz = () -> doBusiness(taskId, ticket, name, st);

        // 优先使用 Engine 的严格执行；若没有，可自行包装
        CompletableFuture<String> inner = engine.supplyFailFast(biz, perTaskTimeoutMs, TimeUnit.MILLISECONDS, true);

        return inner.handle((val, ex) -> {
            if (ex != null) {
                st.status = "FAILED";
                st.error = rootMsg(ex);
                st.endedAt = System.currentTimeMillis();
                Ticket.advanceStage(st, Ticket.Stage.FINISH);
                store.publishUpdate(taskId, ticket);
                // 传播异常（以便 allFailFast 聚合时看到失败）
                throw new CompletionException(ex);
            } else {
                st.status = "SUCCESS";
                st.endedAt = System.currentTimeMillis();
                Ticket.advanceStage(st, Ticket.Stage.FINISH);
                store.publishUpdate(taskId, ticket);
                return new ResultItem(name, val, "SUCCESS", null);
            }
        });
    }

    // =====================================================
    // ==================  核心业务函数  ====================
    // =====================================================

    /**
     * 融合版 doBusiness：
     * - BizContext / SubtaskContext 贯穿
     * - Ticket 阶段推进/进度可视化
     * - 可中断休眠（配合 cancel(true)）
     * - 演示：少量 name 触发异常 (hash & 7 == 0)
     */
    private String doBusiness(String taskId, Ticket ticket, String name, Ticket.Subtask st) {
        final long now = System.currentTimeMillis();
        st.status = "RUNNING";
        st.startedAt = now;
        st.progress.set(0);
        ensureMeta(st);

        BizContext ctx = BizContextHolder.get();
        BizContext.SubtaskContext sc = (ctx != null) ? ctx.sub(name) : null;
        BizContext.Stage stage = (ctx != null) ? ctx.stage("sub:" + name) : null;

        if (stage != null) stage.start().setProgress(5);

        // 业务字段：初始化 path
        if (sc != null) sc.setPath("/root/" + name);
        st.meta.put("path", (sc != null) ? sc.getPath() : ("/root/" + name));
        store.publishUpdate(taskId, ticket);

        try {
            // PREPARE
            Ticket.advanceStage(st, Ticket.Stage.PREPARE);
            st.progress.set(10);
            if (stage != null) stage.setProgress(10);
            store.publishUpdate(taskId, ticket);

            sleepCancellable(20);

            if (sc != null) sc.updatePath(p -> p + "/prepare");
            st.meta.put("path", (sc != null) ? sc.getPath() : st.meta.get("path"));
            store.publishUpdate(taskId, ticket);

            // CALL_DOWNSTREAM
            Ticket.advanceStage(st, Ticket.Stage.CALL_DOWNSTREAM);
            st.progress.set(70);
            if (stage != null) stage.setProgress(70);
            store.publishUpdate(taskId, ticket);

            sleepCancellable(60 + Math.abs(name.hashCode() % 50));

            if ((name.hashCode() & 7) == 0) {
                throw new RuntimeException("boom:" + name);
            }

            if (sc != null) sc.updatePath(p -> p.replace("/prepare", "/called"));
            st.meta.put("path", (sc != null) ? sc.getPath() : st.meta.get("path"));
            if (stage != null) stage.setProgress(85);
            store.publishUpdate(taskId, ticket);

            // MERGE
            Ticket.advanceStage(st, Ticket.Stage.MERGE);
            st.progress.set(90);
            if (stage != null) stage.setProgress(90);
            store.publishUpdate(taskId, ticket);

            sleepCancellable(20);

            if (sc != null) sc.updatePath(p -> p + "/final");
            st.meta.put("path", (sc != null) ? sc.getPath() : st.meta.get("path"));

            // FINISH
            Ticket.advanceStage(st, Ticket.Stage.FINISH);
            st.status = "SUCCESS";
            st.progress.set(100);
            st.endedAt = System.currentTimeMillis();
            if (stage != null) stage.ok();
            store.publishUpdate(taskId, ticket);

            return "ok:" + name;
        } catch (CancellationException ce) {
            st.status = "CANCELLED";
            st.error = "cancelled";
            st.endedAt = System.currentTimeMillis();
            if (stage != null) stage.fail("cancelled");
            Ticket.advanceStage(st, Ticket.Stage.FINISH);
            store.publishUpdate(taskId, ticket);
            throw ce;
        } catch (Throwable e) {
            st.status = "FALLBACK";
            st.error = e.getMessage();
            st.endedAt = System.currentTimeMillis();
            if (stage != null) stage.fail(e.getMessage());
            Ticket.advanceStage(st, Ticket.Stage.FINISH);
            store.publishUpdate(taskId, ticket);
            throw e;
        }
    }

    // =====================================================
    // ====================  工具方法  ======================
    // =====================================================

    /** 幂等收尾：只允许一个分支真正落地（设置 ticket → 聚合 → save/publish） */
    private boolean finalizeOnce(AtomicBoolean flag, Ticket t, Runnable finisher) {
        if (flag.compareAndSet(false, true)) {
            try {
                finisher.run();
            } finally {
                t.recomputeAggregates();
                store.saveTicket(t);
                store.publishUpdate(t.taskId, t);
            }
            return true;
        }
        return false;
    }

    private static void ensureMeta(Ticket.Subtask st) {
        if (st.meta == null) st.meta = new ConcurrentHashMap<>();
    }

    private static void sleepCancellable(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CancellationException("interrupted");
        }
    }

    /** 将多个 CF 合并为 CF<List<T>> （异常在各自 handle 内部消化） */
    private static <T> CompletableFuture<List<T>> allOfList(List<CompletableFuture<T>> cfs) {
        CompletableFuture<?>[] arr = cfs.toArray(new CompletableFuture[0]);
        return CompletableFuture.allOf(arr)
                .thenApply(v -> cfs.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    /** 从 ticket.data 里读出当前 items 的快照（若无则空列表） */
    @SuppressWarnings("unchecked")
    private static List<ResultItem> snapshotItems(Ticket t) {
        Object v = t.data.get("items");
        return (v instanceof List) ? (List<ResultItem>) v : Collections.emptyList();
    }

    /** 统一的 HTTP 响应基础字段 */
    private static Map<String, Object> baseResponse(Ticket t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("taskId", t.taskId);
        m.put("mode", t.mode.name());
        m.put("status", t.status.name());
        m.put("progress", t.progress);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", t.totalCount);
        summary.put("ok", t.okCount);
        summary.put("fallback", t.fallbackCount);
        summary.put("failed", t.failedCount);
        m.put("summary", summary);
        return m;
    }

    private static String rootMsg(Throwable ex) {
        Throwable c = ex;
        while (c.getCause() != null) c = c.getCause();
        return c.getMessage();
    }

    // =====================================================
    // ================  简单结果类型（内置）  ================
    // =====================================================

    /** 简单结果项：避免对外再引入额外依赖类 */
    public static final class ResultItem {
        public final String name;
        public final Object value;
        public final String status; // "SUCCESS" / "FALLBACK"
        public final String error;

        public ResultItem(String name, Object value, String status, String error) {
            this.name = name;
            this.value = value;
            this.status = status;
            this.error = error;
        }
    }
}
