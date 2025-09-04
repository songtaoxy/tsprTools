package com.st.modules.thread.framework.v2;



import org.slf4j.MDC;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 概述：CompletableFuture 企业级并发引擎，统一线程池、超时、重试、限流、熔断、Bulkhead 与并行编排
 * 功能清单：
 * 1）显式线程池与定时调度器
 * 2）MDC 上下文透传
 * 3）per task 超时与全局超时
 * 4）重试与退避策略
 * 5）Bulkhead 并发隔离、令牌桶限流、熔断器
 * 6）allOf anyOf 聚合工具与 withTimeout 附加超时
 * 使用示例：见各方法与 DemoService
 * 注意事项：
 * - 重试仅用于幂等瞬时错误
 * - 限流与熔断配置以引擎维度隔离
 * 入参与出参与异常说明：公开方法均在 Javadoc 中说明
 */
public class CfEngine {

    public static class Builder {
        private String poolName = "biz-pool";
        private int core = 8;
        private int max = 32;
        private int queueCapacity = 256;
        private int bulkheadPermits = 0;
        private TokenBucketLimiter limiter;
        private CircuitBreaker breaker;

        public Builder poolName(String n) { this.poolName = n; return this; }
        public Builder core(int v) { this.core = v; return this; }
        public Builder max(int v) { this.max = v; return this; }
        public Builder queue(int v) { this.queueCapacity = v; return this; }
        public Builder bulkhead(int permits) { this.bulkheadPermits = permits; return this; }
        public Builder rateLimiter(TokenBucketLimiter l) { this.limiter = l; return this; }
        public Builder circuitBreaker(CircuitBreaker b) { this.breaker = b; return this; }
        public CfEngine build() { return new CfEngine(poolName, core, max, queueCapacity, bulkheadPermits, limiter, breaker); }
    }

    private final ThreadPoolExecutor executor;
    private final ScheduledExecutorService scheduler;
    private final Semaphore bulkhead; // 可选
    private final TokenBucketLimiter limiter; // 可选
    private final CircuitBreaker breaker; // 可选

    public static Builder newBuilder() { return new Builder(); }

    public CfEngine(String poolName, int core, int max, int queueCapacity, int bulkheadPermits,
                    TokenBucketLimiter limiter, CircuitBreaker breaker) {
        this.executor = new ThreadPoolExecutor(core, max, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueCapacity),
                new NamedThreadFactory(poolName),
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.scheduler = Executors.newScheduledThreadPool(1, new NamedThreadFactory(poolName + "-timer"));
        this.bulkhead = bulkheadPermits > 0 ? new Semaphore(bulkheadPermits) : null;
        this.limiter = limiter;
        this.breaker = breaker;
    }

    /**
     * 概述：单任务执行，带超时、降级、限流、熔断、Bulkhead
     * 功能清单：限流与熔断前置校验、超时失败占位、异常转降级
     * 使用示例：engine.supply(() -> dao.load(id), 120, MILLISECONDS, () -> defaultVal)
     * 注意事项：fallback 不应抛异常
     * 入参：task 业务任务；timeout 时长；tu 单位；fallback 降级
     * 出参：CompletableFuture<T> 异常：内部以 exceptionally 或 fallback 收敛
     */
    public <T> CompletableFuture<T> supply(Supplier<T> task, long timeout, TimeUnit tu, Supplier<T> fallback) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        Supplier<T> guarded = () -> {
            preCheck();
            acquire();
            try { return task.get(); }
            finally { release(); }
        };
        CompletableFuture<T> cf = CompletableFuture.supplyAsync(() -> callWithMdc(guarded, mdc), executor);
        CompletableFuture<T> tmo = failAfter(timeout, tu);
        return cf.applyToEither(tmo, Function.identity())
                .whenComplete((val, err) -> notifyBreaker(err))
                .exceptionally(ex -> safeGet(fallback));
    }

    /**
     * 概述：单任务执行 + 重试（固定或指数退避）
     * 功能清单：attempts 次尝试，间隔 Backoff
     * 使用示例：engine.retry(3, Backoff.expo(50, 200, MILLISECONDS), () -> http.get())
     * 注意事项：仅幂等瞬时错误使用；总时长可能超出单次超时
     * 入参：attempts 次数；backoff 退避策略；supplier 任务
     * 出参：CompletableFuture<T> 异常：最终失败 exceptionally 抛出
     */
    public <T> CompletableFuture<T> retry(int attempts, Backoff backoff, Supplier<T> supplier) {
        CompletableFuture<T> promise = new CompletableFuture<T>();
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        attemptRetry(promise, attempts, 1, backoff, supplier, mdc);
        return promise;
    }

    /**
     * 概述：并行执行多任务，单个失败用 fallback 填充，整体可设全局超时
     * 功能清单：allOf 聚合、异常隔离、全局 deadline
     * 使用示例：engine.allWithFallback(tasks, ex -> def, 200, MILLISECONDS)
     * 注意事项：fallback 不应抛异常
     * 入参：tasks 任务列表；fallback 单个失败降级；timeout 全局超时；tu 单位
     * 出参：CompletableFuture<List<T>> 异常：全局超时 exceptionally 抛出
     */
    public <T> CompletableFuture<List<T>> allWithFallback(List<Supplier<T>> tasks, Function<Throwable, T> fallback,
                                                          long timeout, TimeUnit tu) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        List<CompletableFuture<T>> cfs = new ArrayList<CompletableFuture<T>>(tasks.size());
        for (Supplier<T> s : tasks) {
            CompletableFuture<T> cf = CompletableFuture.supplyAsync(() -> callWithMdc(() -> {
                        preCheck();
                        acquire();
                        try { return s.get(); }
                        finally { release(); }
                    }, mdc), executor).whenComplete((val, err) -> notifyBreaker(err))
                    .exceptionally(ex -> fallback.apply(ex));
            cfs.add(cf);
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0]));
        CompletableFuture<Void> tmo = failAfter(timeout, tu);
        return all.applyToEither(tmo, new Function<Void, Void>() { public Void apply(Void v) { return v; } })
                .thenApply(new Function<Void, List<T>>() {
                    public List<T> apply(Void v) {
                        List<T> out = new ArrayList<T>(cfs.size());
                        for (CompletableFuture<T> f : cfs) out.add(f.join());
                        return out;
                    }
                });
    }

    /**
     * 概述：任一任务先完成即返回，带超时与最终降级
     * 功能清单：anyOf + 超时 + 异常降级
     * 使用示例：engine.anyOfOrFallback(tasks, def, 150, MILLISECONDS)
     * 注意事项：必要时对返回值做校验
     * 入参：tasks；fallback；timeout；tu
     * 出参：CompletableFuture<T> 异常：异常或超时返回 fallback
     */
    public <T> CompletableFuture<T> anyOfOrFallback(List<Supplier<T>> tasks, final T fallback, long timeout, TimeUnit tu) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        List<CompletableFuture<T>> cfs = new ArrayList<CompletableFuture<T>>(tasks.size());
        for (Supplier<T> s : tasks) {
            cfs.add(CompletableFuture.supplyAsync(() -> callWithMdc(() -> {
                preCheck();
                acquire();
                try { return s.get(); }
                finally { release(); }
            }, mdc), executor).whenComplete((val, err) -> notifyBreaker(err)));
        }
        CompletableFuture<T> any = CompletableFuture.anyOf(cfs.toArray(new CompletableFuture[0]))
                .thenApply(new Function<Object, T>() { public T apply(Object o) { return (T) o; } });
        CompletableFuture<T> tmo = failAfter(timeout, tu);
        return any.applyToEither(tmo, new Function<T, T>() { public T apply(T t) { return t; } })
                .exceptionally(new Function<Throwable, T>() { public T apply(Throwable ex) { return fallback; } });
    }

    /**
     * 概述：为已有 CompletableFuture 附加超时
     * 功能清单：超时后以异常完成
     * 使用示例：engine.withTimeout(cf, 80, MILLISECONDS)
     * 注意事项：不取消原任务
     * 入参：cf 原始；timeout；tu
     * 出参：CompletableFuture<T> 异常：超时抛 TimeoutException
     */
    public <T> CompletableFuture<T> withTimeout(CompletableFuture<T> cf, long timeout, TimeUnit tu) {
        CompletableFuture<T> tmo = failAfter(timeout, tu);
        return cf.applyToEither(tmo, new Function<T, T>() { public T apply(T t) { return t; } });
    }

    /**
     * 概述：线程池指标快照
     * 功能清单：读取核心指标
     * 使用示例：engine.snapshot()
     * 注意事项：仅快照
     * 入参与出参与异常说明：无
     */
    public PoolMetrics snapshot() {
        BlockingQueue<Runnable> q = executor.getQueue();
        return new PoolMetrics(
                executor.getCorePoolSize(),
                executor.getMaximumPoolSize(),
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getLargestPoolSize(),
                executor.getTaskCount(),
                executor.getCompletedTaskCount(),
                q.size(),
                q.remainingCapacity()
        );
    }

    /**
     * 概述：优雅关闭
     * 功能清单：shutdown + awaitTermination
     * 使用示例：engine.shutdownGracefully(20, SECONDS)
     * 注意事项：关闭后不可再提交任务
     * 入参：timeout；tu
     * 出参：boolean 是否在时限内完成
     * 异常说明：InterruptedException 被捕获并重置中断标记
     */
    public boolean shutdownGracefully(long timeout, TimeUnit tu) {
        executor.shutdown();
        scheduler.shutdown();
        try {
            boolean a = executor.awaitTermination(timeout, tu);
            boolean b = scheduler.awaitTermination(timeout, tu);
            return a && b;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private <T> void attemptRetry(final CompletableFuture<T> promise, final int attempts, final int attemptIdx,
                                  final Backoff backoff, final Supplier<T> supplier, final Map<String, String> mdc) {
        CompletableFuture.supplyAsync(new Supplier<T>() {
            public T get() {
                preCheck();
                acquire();
                try { return callWithMdc(supplier, mdc); }
                finally { release(); }
            }
        }, executor).whenComplete(new BiCompletion<T>(promise, attempts, attemptIdx, backoff, supplier, mdc));
    }

    private class BiCompletion<T> implements java.util.function.BiConsumer<T, Throwable> {
        private final CompletableFuture<T> promise;
        private final int attempts;
        private final int attemptIdx;
        private final Backoff backoff;
        private final Supplier<T> supplier;
        private final Map<String, String> mdc;

        BiCompletion(CompletableFuture<T> promise, int attempts, int attemptIdx, Backoff backoff,
                     Supplier<T> supplier, Map<String, String> mdc) {
            this.promise = promise; this.attempts = attempts; this.attemptIdx = attemptIdx;
            this.backoff = backoff; this.supplier = supplier; this.mdc = mdc;
        }

        public void accept(T val, Throwable err) {
            notifyBreaker(err);
            if (err == null) { promise.complete(val); return; }
            if (attemptIdx >= attempts) { promise.completeExceptionally(err); return; }
            long delay = backoff.nextDelayMillis(attemptIdx);
            scheduler.schedule(new Runnable() {
                public void run() { attemptRetry(promise, attempts, attemptIdx + 1, backoff, supplier, mdc); }
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    private <T> CompletableFuture<T> failAfter(long timeout, TimeUnit tu) {
        final CompletableFuture<T> p = new CompletableFuture<T>();
        scheduler.schedule(new Runnable() {
            public void run() { p.completeExceptionally(new TimeoutException("timeout " + timeout + " " + tu)); }
        }, timeout, tu);
        return p;
    }

    private void preCheck() {
        if (limiter != null && !limiter.tryAcquire(1)) throw new RejectedExecutionException("rate limited");
        if (breaker != null && !breaker.tryPass()) throw new RejectedExecutionException("circuit open");
    }

    private void notifyBreaker(Throwable err) {
        if (breaker == null) return;
        if (err == null) breaker.onSuccess(); else breaker.onFailure();
    }

    private void acquire() {
        if (bulkhead != null && !bulkhead.tryAcquire()) throw new RejectedExecutionException("bulkhead full");
    }

    private void release() {
        if (bulkhead != null) bulkhead.release();
    }

    private static <T> T callWithMdc(Supplier<T> s, Map<String, String> mdc) {
        Map<String, String> old = MDC.getCopyOfContextMap();
        try { if (mdc != null) MDC.setContextMap(mdc); else MDC.clear(); return s.get(); }
        finally { if (old != null) MDC.setContextMap(old); else MDC.clear(); }
    }

    private static <T> T safeGet(Supplier<T> s) { try { return s.get(); } catch (Throwable e) { return null; } }
}

