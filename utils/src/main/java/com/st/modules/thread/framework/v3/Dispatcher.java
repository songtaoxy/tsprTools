package com.st.modules.thread.framework.v3;


import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** 任务调度适配器：聚焦“单次执行/重试执行 + 回调” */
public final class Dispatcher {
    private final Engine engine;
    public Dispatcher(Engine engine) { this.engine = engine; }

    public <T> void dispatch(AsyncTaskContext ctx, Callable<T> task, Callback<T> cb, long timeoutMs, boolean cancelOnTimeout) {
        ctx.running();
        CompletableFuture<T> cf = engine.supply(() -> {
            try { return task.call(); } catch (Exception e) { throw new RuntimeException(e); }
        }, timeoutMs, TimeUnit.MILLISECONDS, () -> null, cancelOnTimeout);
        cf.whenComplete((val, err) -> {
            if (err == null) cb.onSuccess(ctx, val);
            else cb.onFailure(ctx, err);
        });
    }

    public <T> void dispatchWithRetry(AsyncTaskContext ctx, Callable<T> task, Callback<T> cb, int attempts, Backoff backoff) {
        ctx.running();
        engine.retry(attempts, backoff, () -> { try { return task.call(); } catch (Exception e) { throw new RuntimeException(e); } })
                .whenComplete((val, err) -> {
                    if (err == null) cb.onSuccess(ctx, val); else cb.onFailure(ctx, err);
                });
    }
}
