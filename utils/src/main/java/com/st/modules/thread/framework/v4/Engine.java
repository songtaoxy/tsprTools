package com.st.modules.thread.framework.v4;


import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;

/** 企业级轻量并发引擎（JDK8）：见类注释顶部的功能列表 */
public final class Engine {

    /* ============== Builder ============== */
    public static final class Builder {
        private ExecutorService executor;
        private ScheduledExecutorService scheduler;
        private TaskWrapper taskWrapper = TaskWrapper.NOOP;

        private String poolName = "biz";
        private int core = Math.max(2, Runtime.getRuntime().availableProcessors());
        private int max  = Math.max(core, core * 2);
        private int queue = 256;
        private long keepAliveSeconds = 60;

        public Builder executor(ExecutorService ex){ this.executor = ex; return this; }
        public Builder scheduler(ScheduledExecutorService sch){ this.scheduler = sch; return this; }
        public Builder taskWrapper(TaskWrapper w){ this.taskWrapper = (w==null?TaskWrapper.NOOP:w); return this; }

        public Builder poolName(String n){ this.poolName = n; return this; }
        public Builder core(int n){ this.core=n; return this; }
        public Builder max(int n){ this.max=n; return this; }
        public Builder queue(int q){ this.queue=q; return this; }
        public Builder keepAliveSeconds(long s){ this.keepAliveSeconds=s; return this; }

        public Engine build() {
            if (executor==null) {
                final String pn = poolName;
                executor = new ThreadPoolExecutor(
                        core, max, keepAliveSeconds, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(queue),
                        new ThreadFactory() {
                            private final ThreadFactory df = Executors.defaultThreadFactory();
                            public Thread newThread(Runnable r){
                                Thread t = df.newThread(r);
                                t.setName(pn+"-engine-"+t.getId());
                                t.setDaemon(false);
                                return t;
                            }
                        },
                        new ThreadPoolExecutor.CallerRunsPolicy()
                );
            }
            if (scheduler==null) {
                scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                    public Thread newThread(Runnable r){ Thread t=new Thread(r, poolName+"-timer"); t.setDaemon(true); return t; }
                });
            }
            return new Engine(executor, scheduler, taskWrapper);
        }
    }
    public static Builder newBuilder(){ return new Builder(); }

    /* ============== Fields & ctor ============== */
    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;
    private final TaskWrapper taskWrapper;

    private Engine(ExecutorService ex, ScheduledExecutorService sch, TaskWrapper tw){
        this.executor=ex; this.scheduler=sch; this.taskWrapper=(tw==null?TaskWrapper.NOOP:tw);
    }

    /* ============== Common ============== */

    /** 外层时限：不取消底层，仅让 out 以 TimeoutException 失败 */
    public <T> CompletableFuture<T> withTimeout(CompletableFuture<T> cf, long timeout, TimeUnit unit){
        if (timeout<=0) return cf;
        final CompletableFuture<T> out = new CompletableFuture<T>();
        cf.whenComplete(new BiConsumer<T, Throwable>() {
            public void accept(T v, Throwable e){ if (e==null) out.complete(v); else out.completeExceptionally(e); }
        });
        scheduler.schedule(new Runnable() {
            public void run(){ if (!out.isDone()) out.completeExceptionally(new TimeoutException("engine.withTimeout")); }
        }, timeout, unit);
        return out;
    }

    private <T> Future<T> submit(Callable<T> c){ return executor.submit(taskWrapper.wrap(c)); }

    private static <T> void completeWithFallback(CompletableFuture<T> cf, Supplier<T> fb, Throwable cause){
        if (cf.isDone()) return;
        try { cf.complete(fb==null?null:fb.get()); }
        catch (Throwable ex){ if (cause!=null) ex.addSuppressed(cause); cf.completeExceptionally(ex); }
    }

    /* ============== 单任务 ============== */

    /** 有损：失败或到时 → fallback；到时是否中断由 cancelOnTimeout 决定 */
    public <T> CompletableFuture<T> supply(final Supplier<T> supplier,
                                           final long timeout, final TimeUnit unit,
                                           final Supplier<T> fallback,
                                           final boolean cancelOnTimeout){
        final CompletableFuture<T> cf = new CompletableFuture<T>();
        final Future<T> f;
        try { f = submit(new Callable<T>() { public T call() throws Exception { return supplier.get(); }}); }
        catch (RejectedExecutionException rex){ completeWithFallback(cf, fallback, rex); return cf; }

        executor.execute(new Runnable() {
            public void run() {
                try { cf.complete(f.get()); }
                catch (CancellationException e){ completeWithFallback(cf, fallback, e); }
                catch (InterruptedException e){ Thread.currentThread().interrupt(); completeWithFallback(cf, fallback, e); }
                catch (ExecutionException e){ completeWithFallback(cf, fallback, e.getCause()); }
            }
        });
        if (timeout>0) {
            scheduler.schedule(new Runnable() {
                public void run(){
                    if (cf.isDone()) return;
                    if (cancelOnTimeout) f.cancel(true);
                    completeWithFallback(cf, fallback, new TimeoutException("engine.supply timeout"));
                }
            }, timeout, unit);
        }
        return cf;
    }

    /** 无超时的有损单任务：失败 → fallback */
    public <T> CompletableFuture<T> supplyNoTimeout(final Supplier<T> supplier, final Supplier<T> fallback){
        final CompletableFuture<T> cf = new CompletableFuture<T>();
        final Future<T> f;
        try { f = submit(new Callable<T>() { public T call() throws Exception { return supplier.get(); }}); }
        catch (RejectedExecutionException rex){ completeWithFallback(cf, fallback, rex); return cf; }

        executor.execute(new Runnable() {
            public void run() {
                try { cf.complete(f.get()); }
                catch (CancellationException e){ completeWithFallback(cf, fallback, e); }
                catch (InterruptedException e){ Thread.currentThread().interrupt(); completeWithFallback(cf, fallback, e); }
                catch (ExecutionException e){ completeWithFallback(cf, fallback, e.getCause()); }
            }
        });
        return cf;
    }

    /** Fail-Fast 单任务：异常直接异常完成；到时是否中断由 cancelOnTimeout 决定 */
    public <T> CompletableFuture<T> supplyFailFast(final Supplier<T> supplier,
                                                   final long timeout, final TimeUnit unit,
                                                   final boolean cancelOnTimeout){
        final CompletableFuture<T> cf = new CompletableFuture<T>();
        final Future<T> f;
        try { f = submit(new Callable<T>() { public T call() throws Exception { return supplier.get(); }}); }
        catch (RejectedExecutionException rex){ cf.completeExceptionally(rex); return cf; }

        executor.execute(new Runnable() {
            public void run() {
                try { cf.complete(f.get()); }
                catch (CancellationException e){ cf.completeExceptionally(e); }
                catch (InterruptedException e){ Thread.currentThread().interrupt(); cf.completeExceptionally(e); }
                catch (ExecutionException e){ cf.completeExceptionally(e.getCause()); }
            }
        });
        if (timeout>0) {
            scheduler.schedule(new Runnable() {
                public void run(){
                    if (!cf.isDone()) {
                        if (cancelOnTimeout) f.cancel(true);
                        cf.completeExceptionally(new TimeoutException("engine.supplyFailFast timeout"));
                    }
                }
            }, timeout, unit);
        }
        return cf;
    }

    /* ============== 并行聚合 ============== */

    /** 有损聚合：每个子任务失败 → fallbackFn 生成占位；outerTimeout 到点则整个 out 以 TimeoutException 失败 */
    public <T> CompletableFuture<List<T>> allWithFallback(final List<? extends Supplier<T>> tasks,
                                                          final Function<Throwable, T> fallbackFn,
                                                          final long outerTimeout, final TimeUnit unit){
        final List<CompletableFuture<T>> cfs = new ArrayList<CompletableFuture<T>>(tasks.size());
        for (final Supplier<T> s : tasks) {
            cfs.add(supplyNoTimeout(s, new Supplier<T>() { public T get(){ return fallbackFn.apply(null); }}));
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0]));
        CompletableFuture<List<T>> joined = all.thenApply(new Function<Void, List<T>>() {
            public List<T> apply(Void v) {
                List<T> out = new ArrayList<T>(cfs.size());
                for (CompletableFuture<T> cf : cfs) out.add(cf.join());
                return out;
            }
        });
        return outerTimeout>0 ? withTimeout(joined, outerTimeout, unit) : joined;
    }

    /** Fail-Fast 聚合：任一失败立刻异常；可选取消其他在途 */
    public <T> CompletableFuture<List<T>> allFailFast(final List<? extends Supplier<T>> tasks,
                                                      final long outerTimeout, final TimeUnit unit,
                                                      final boolean cancelOnFailure){
        final CompletableFuture<List<T>> out = new CompletableFuture<List<T>>();
        final List<CompletableFuture<T>> cfs = new ArrayList<CompletableFuture<T>>(tasks.size());

        for (final Supplier<T> s : tasks) {
            final CompletableFuture<T> cf = supplyFailFast(s, 0, TimeUnit.MILLISECONDS, false);
            cfs.add(cf);
            cf.whenComplete(new BiConsumer<T, Throwable>() {
                public void accept(T v, Throwable e) {
                    if (out.isDone()) return;
                    if (e != null) {
                        if (cancelOnFailure) {
                            for (CompletableFuture<?> other : cfs) if (other != cf && !other.isDone()) other.cancel(true);
                        }
                        out.completeExceptionally(e);
                    } else {
                        boolean allDone = true;
                        for (CompletableFuture<?> o : cfs) if (!o.isDone()) { allDone=false; break; }
                        if (allDone) {
                            List<T> rs = new ArrayList<T>(cfs.size());
                            for (CompletableFuture<T> o : cfs) rs.add(o.join());
                            out.complete(rs);
                        }
                    }
                }
            });
        }

        return outerTimeout>0 ? withTimeout(out, outerTimeout, unit) : out;
    }

    /** 多副本竞速：任一成功即成功；全部失败或到时→fallback 值（不抛异常） */
    public <T> CompletableFuture<T> anyOfOrFallback(final List<? extends Supplier<T>> replicas,
                                                    final Supplier<T> fallback,
                                                    final long timeout, final TimeUnit unit){
        final CompletableFuture<T> out = new CompletableFuture<T>();
        final AtomicBoolean done = new AtomicBoolean(false);
        final List<CompletableFuture<T>> cfs = new ArrayList<CompletableFuture<T>>(replicas.size());

        for (final Supplier<T> s : replicas) {
            CompletableFuture<T> cf = supplyFailFast(s, 0, TimeUnit.MILLISECONDS, false);
            cfs.add(cf);
            cf.whenComplete(new BiConsumer<T, Throwable>() {
                public void accept(T v, Throwable e) {
                    if (done.get()) return;
                    if (e == null) {
                        if (done.compareAndSet(false, true)) out.complete(v);
                    } else {
                        boolean allDone = true;
                        for (CompletableFuture<?> o : cfs) if (!o.isDone()) { allDone=false; break; }
                        if (allDone && done.compareAndSet(false, true)) completeWithFallback(out, fallback, e);
                    }
                }
            });
        }

        if (timeout>0) {
            scheduler.schedule(new Runnable() {
                public void run(){ if (done.compareAndSet(false,true)) completeWithFallback(out, fallback, new TimeoutException("engine.anyOf timeout")); }
            }, timeout, unit);
        }
        return out;
    }

    /* ============== 重试（同步，外层可再交给 supply 异步化） ============== */

    public static <T> T retrySync(final Supplier<T> attempt1,
                                  final Predicate<Throwable> shouldRetry,
                                  final int maxAttempts,
                                  final long baseDelayMs,
                                  final long maxDelayMs,
                                  final double jitterRatio) throws Exception {
        if (maxAttempts <= 0) throw new IllegalArgumentException("maxAttempts<=0");
        int attempt=0; Throwable last=null; Random rnd=new Random();
        while (attempt < maxAttempts) {
            attempt++;
            try { return attempt1.get(); }
            catch (Throwable e){
                last=e;
                if (attempt>=maxAttempts) break;
                if (shouldRetry!=null && !shouldRetry.test(e)) break;
                long delay = Math.min(maxDelayMs, (long)(baseDelayMs * Math.pow(2, attempt-1)));
                if (jitterRatio>0){
                    double j = 1.0 + (rnd.nextDouble()*2 - 1) * jitterRatio; // ±jitter
                    delay = Math.max(1, (long)(delay * j));
                }
                try { Thread.sleep(delay); } catch (InterruptedException ie){ Thread.currentThread().interrupt(); throw ie; }
            }
        }
        if (last instanceof Exception) throw (Exception) last;
        throw new Exception(last);
    }

    /* ============== 关闭 ============== */

    public void shutdown(){
        executor.shutdown();
        scheduler.shutdown();
    }
}
