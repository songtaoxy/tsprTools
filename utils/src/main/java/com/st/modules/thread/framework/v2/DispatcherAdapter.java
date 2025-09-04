package com.st.modules.thread.framework.v2;


import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;

/**
 * 概述：调度适配器，基于 CfEngine 提供超时、重试、限流、熔断与MDC透传
 * 功能清单：
 * 1）dispatch：单任务提交，带超时与降级回调
 * 2）dispatchWithRetry：有限次重试与退避
 * 使用示例：adapter.dispatch(ctx, () -> core.handle(..), cb, 120, MILLISECONDS)
 * 注意事项：Callable 内部业务逻辑应幂等（尤其是重试场景）
 * 入参与出参与异常说明：回调收敛异常；不抛检查异常
 */
public class DispatcherAdapter {
    private final CfEngine engine;

    public DispatcherAdapter(CfEngine engine) {
        this.engine = engine;
    }

    public <T> void dispatch(final AsyncTaskContext ctx,
                             final Callable<T> taskLogic,
                             final Callback<T> callback,
                             final long timeout,
                             final TimeUnit tu) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        ctx.running();
        CompletableFuture<T> cf = engine.supply(new java.util.function.Supplier<T>() {
            public T get() {
                try { return taskLogic.call(); } catch (Exception e) { throw new RuntimeException(e); }
            }
        }, timeout, tu, new java.util.function.Supplier<T>() { public T get() { return null; } });
        cf.whenComplete(new java.util.function.BiConsumer<T, Throwable>() {
            public void accept(T val, Throwable err) {
                if (err == null && val != null) {
                    callback.onSuccess(ctx, val);
                } else {
                    callback.onFailure(ctx, err != null ? err : new RuntimeException("timeout or null result"));
                }
            }
        });
    }

    public <T> void dispatchWithRetry(final AsyncTaskContext ctx,
                                      final Callable<T> taskLogic,
                                      final Callback<T> callback,
                                      final int attempts,
                                      final Backoff backoff) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        ctx.running();
        CompletableFuture<T> cf = engine.retry(attempts, backoff, new java.util.function.Supplier<T>() {
            public T get() {
                try { return taskLogic.call(); } catch (Exception e) { throw new RuntimeException(e); }
            }
        });
        cf.whenComplete(new java.util.function.BiConsumer<T, Throwable>() {
            public void accept(T val, Throwable err) {
                if (err == null) callback.onSuccess(ctx, val); else callback.onFailure(ctx, err);
            }
        });
    }
}

