package com.st.modules.thread.framework.v3;


import org.slf4j.MDC;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 企业级并发引擎：
 * - 统一：线程池、定时调度、超时、重试、限流、熔断、Bulkhead、MDC
 * - 编排：allOf / anyOf / 附加超时 / 全局 Deadline 辅助
 * - 取消：可选超时到点取消（interrupt），需业务检查中断以安全退出
 */
public final class Engine {

    /* ---------- Builder ---------- */
    public static final class Builder {
        private String poolName = "biz";
        private int core = 8, max = 32, queue = 256;
        private long keepAliveSeconds = 60;
        private int bulkheadPermits = 0;
        private TokenBucketLimiter limiter;
        private CircuitBreaker breaker;
        private MetricsSink metrics = MetricsSink.NOOP;
        private RejectedExecutionHandler rejectionHandler = new ThreadPoolExecutor.CallerRunsPolicy();

        public Builder poolName(String v) { poolName = v; return this; }
        public Builder core(int v) { core = v; return this; }
        public Builder max(int v) { max = v; return this; }
        public Builder queue(int v) { queue = v; return this; }
        public Builder keepAliveSeconds(long s) { keepAliveSeconds = s; return this; }
        public Builder bulkhead(int permits) { bulkheadPermits = permits; return this; }
        public Builder rateLimiter(TokenBucketLimiter l) { limiter = l; return this; }
        public Builder circuitBreaker(CircuitBreaker b) { breaker = b; return this; }
        public Builder metricsSink(MetricsSink m) { metrics = (m==null?MetricsSink.NOOP:m); return this; }
        public Builder rejectionHandler(RejectedExecutionHandler h) { rejectionHandler = h; return this; }
        public Engine build() { return new Engine(poolName, core, max, queue, keepAliveSeconds, bulkheadPermits, limiter, breaker, metrics, rejectionHandler); }
    }
    public static Builder newBuilder() { return new Builder(); }

    /* ---------- Fields ---------- */
    private final String poolName;
    private final ThreadPoolExecutor executor;
    private final ScheduledExecutorService scheduler;
    private final Semaphore bulkhead;
    private volatile TokenBucketLimiter limiter; // 可替换
    private volatile CircuitBreaker breaker;     // 可替换
    private final MetricsSink metrics;

    private Engine(String poolName, int core, int max, int queue, long keepAliveSeconds, int bulkheadPermits,
                   TokenBucketLimiter limiter, CircuitBreaker breaker, MetricsSink metrics, RejectedExecutionHandler handler) {
        this.poolName = poolName;
        this.executor = new ThreadPoolExecutor(core, max, keepAliveSeconds, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queue),
                new NamedThreadFactory(poolName),
                handler);
        this.scheduler = Executors.newScheduledThreadPool(1, new NamedThreadFactory(poolName + "-timer"));
        this.bulkhead = bulkheadPermits > 0 ? new Semaphore(bulkheadPermits) : null;
        this.limiter = limiter; this.breaker = breaker; this.metrics = metrics;
    }

    /* ---------- 基础 API ---------- */

    /** 单任务执行 + 超时 +（可选）到点取消 + 降级回退 */
    public <T> CompletableFuture<T> supply(Supplier<T> supplier, long timeout, TimeUnit unit,
                                           Supplier<T> fallback, boolean cancelOnTimeout) {
        Map<String,String> mdc = MDC.getCopyOfContextMap();
        // 限流/熔断：尽量在提交前短路
        if (!preCheckFast()) return failing(new RejectedExecutionException("rate limited or circuit open"));
        acquireBulkheadOrFail();

        metrics.onSubmit(poolName, "supply");

        final CompletableFuture<T> promise = new CompletableFuture<T>();
        final Callable<T> task = MdcPropagator.wrap(() -> supplier.get(), mdc);
        final Future<T> future;
        try {
            future = executor.submit(new Callable<T>() {
                @Override public T call() throws Exception {
                    try { return task.call(); }
                    finally { releaseBulkhead(); }
                }
            });
        } catch (RejectedExecutionException rex) {
            releaseBulkhead();
            metrics.onRejected(poolName, "executor_rejected");
            return failing(rex);
        }

        // 结果桥接
        executor.execute(() -> {
            try {
                T v = future.get();
                notifyBreaker(null); metrics.onComplete(poolName, "supply", true);
                promise.complete(v);
            } catch (CancellationException ce) {
                notifyBreaker(ce); metrics.onComplete(poolName, "supply", false);
                promise.completeExceptionally(ce);
            } catch (ExecutionException ee) {
                notifyBreaker(ee.getCause()); metrics.onComplete(poolName, "supply", false);
                promise.completeExceptionally(ee.getCause());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                notifyBreaker(ie); metrics.onComplete(poolName, "supply", false);
                promise.completeExceptionally(ie);
            }
        });

        // 超时器
        scheduler.schedule(() -> {
            if (promise.isDone()) return;
            if (cancelOnTimeout) future.cancel(true); // 触发线程中断，需业务检查中断点
            promise.completeExceptionally(new TimeoutException("timeout " + timeout + " " + unit));
            notifyBreaker(new TimeoutException("timeout"));
            metrics.onComplete(poolName, "supply", false);
        }, timeout, unit);

        // 回退（以 exceptionally 统一）
        return promise.exceptionally(ex -> safeGet(fallback));
    }

    /** 无取消版本（常规场景，语义与 CompletableFuture.supplyAsync + timeout 等价） */
    public <T> CompletableFuture<T> supply(Supplier<T> supplier, long timeout, TimeUnit unit, Supplier<T> fallback) {
        return supply(supplier, timeout, unit, fallback, false);
    }

    /** 重试（固定/指数/抖动），幂等瞬时错误使用 */
    public <T> CompletableFuture<T> retry(int attempts, Backoff backoff, Supplier<T> supplier) {
        final CompletableFuture<T> promise = new CompletableFuture<T>();
        attemptRetry(promise, attempts, 1, backoff, supplier);
        return promise;
    }

    /** 为已有 CF 附加超时（不取消原任务） */
    public <T> CompletableFuture<T> withTimeout(CompletableFuture<T> cf, long timeout, TimeUnit unit) {
        CompletableFuture<T> tmo = new CompletableFuture<T>();
        scheduler.schedule(() -> tmo.completeExceptionally(new TimeoutException("timeout " + timeout + " " + unit)), timeout, unit);
        return cf.applyToEither(tmo, Function.identity());
    }

    /** 并行聚合：allOf + 单个失败使用 fallback 填充；可配全局超时 */
    public <T> CompletableFuture<List<T>> allWithFallback(List<Supplier<T>> tasks,
                                                          Function<Throwable,T> fallback, long timeout, TimeUnit unit) {
        List<CompletableFuture<T>> cfs = new ArrayList<CompletableFuture<T>>(tasks.size());
        for (Supplier<T> s : tasks) {
            cfs.add(supply(s, timeout, unit, () -> null).exceptionally(ex -> fallback.apply(ex)));
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0]));
        return all.thenApply(v -> {
            List<T> out = new ArrayList<T>(cfs.size());
            for (CompletableFuture<T> f : cfs) out.add(f.join());
            return out;
        });
    }

    /** 任一完成返回：anyOf + 超时 + 最终回退 */
    public <T> CompletableFuture<T> anyOfOrFallback(List<Supplier<T>> tasks, T fallback, long timeout, TimeUnit unit) {
        List<CompletableFuture<T>> cfs = new ArrayList<CompletableFuture<T>>(tasks.size());
        for (Supplier<T> s : tasks) cfs.add(supply(s, timeout, unit, () -> null));
        CompletableFuture<Object> any = CompletableFuture.anyOf(cfs.toArray(new CompletableFuture[0]));
        CompletableFuture<T> tmo = new CompletableFuture<T>();
        scheduler.schedule(() -> tmo.completeExceptionally(new TimeoutException("timeout")), timeout, unit);
        return any.applyToEither(tmo, o -> (T)o).exceptionally(ex -> fallback);
    }

    /* ---------- 辅助：Deadline ---------- */

    /** 使用全局 Deadline 自动换算每步剩余时间（ms），<=0 则立即抛超时 */
    public static long stepTimeoutMs(Deadline ddl, long defaultMs) {
        if (ddl == null) return defaultMs;
        long left = ddl.leftMillis();
        if (left <= 0) throw new RuntimeException(new TimeoutException("deadline expired"));
        return Math.min(left, defaultMs);
    }

    /* ---------- 观测与生命周期 ---------- */

    public PoolMetrics snapshot() {
        BlockingQueue<Runnable> q = executor.getQueue();
        return new PoolMetrics(executor.getCorePoolSize(), executor.getMaximumPoolSize(),
                executor.getPoolSize(), executor.getActiveCount(), executor.getLargestPoolSize(),
                executor.getTaskCount(), executor.getCompletedTaskCount(),
                q.size(), q.remainingCapacity());
    }

    /** 运行时调整核心/最大线程数（队列大小不可直接改） */
    public void resize(int newCore, int newMax) {
        if (newCore <= 0 || newMax < newCore) throw new IllegalArgumentException("bad sizes");
        executor.setCorePoolSize(newCore);
        executor.setMaximumPoolSize(newMax);
    }

    public boolean shutdownGracefully(long timeout, TimeUnit unit) {
        executor.shutdown(); scheduler.shutdown();
        try { return executor.awaitTermination(timeout, unit) && scheduler.awaitTermination(timeout, unit); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); return false; }
    }

    /* ---------- 内部：retry/治理/透传 ---------- */

    private <T> void attemptRetry(CompletableFuture<T> promise, int attempts, int idx, Backoff backoff, Supplier<T> supplier) {
        // 单次尝试：包装为 supply（使用较大超时；由 backoff 控制间隔）
        supply(supplier, Long.MAX_VALUE, TimeUnit.DAYS, () -> null)
                .whenComplete((v, e) -> {
                    if (e == null) { promise.complete(v); return; }
                    if (idx >= attempts) { promise.completeExceptionally(e); return; }
                    long delay = backoff.nextDelayMillis(idx);
                    scheduler.schedule(() -> attemptRetry(promise, attempts, idx+1, backoff, supplier),
                            delay, TimeUnit.MILLISECONDS);
                });
    }

    private boolean preCheckFast() {
        if (limiter != null && !limiter.tryAcquire(1)) return false;
        if (breaker != null && !breaker.tryPass()) return false;
        return true;
    }

    private void acquireBulkheadOrFail() {
        if (bulkhead != null && !bulkhead.tryAcquire())
            throw new RejectedExecutionException("bulkhead full");
    }
    private void releaseBulkhead() { if (bulkhead != null) bulkhead.release(); }

    private void notifyBreaker(Throwable err) {
        if (breaker == null) return;
        if (err == null) breaker.onSuccess(); else breaker.onFailure();
    }

    private static <T> CompletableFuture<T> failing(Throwable ex) {
        CompletableFuture<T> cf = new CompletableFuture<T>();
        cf.completeExceptionally(ex); return cf;
    }

    private static <T> T safeGet(Supplier<T> s) { try { return s == null ? null : s.get(); } catch (Throwable e) { return null; } }

    /* 可切换限流/熔断策略 */
    public void setLimiter(TokenBucketLimiter limiter) { this.limiter = limiter; }
    public void setBreaker(CircuitBreaker breaker) { this.breaker = breaker; }
    public String poolName() { return poolName; }
}
