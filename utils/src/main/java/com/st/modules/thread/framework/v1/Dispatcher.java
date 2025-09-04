package com.st.modules.thread.framework.v1;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@NoArgsConstructor
public class Dispatcher {

    public <T> void dispatch(AsyncTaskContext ctx, Callable<T> taskLogic, Callback<T> callback, Executor executor) {
        CompletableFuture.supplyAsync(() -> {
            ctx.getMetadata().put("status", "RUNNING");
            try {
                T result = taskLogic.call();
                ctx.complete(result);
                callback.onSuccess(result);
                return result;
            } catch (Exception ex) {
                ctx.fail(ex);
                callback.onFailure(ex);
                return null;
            }
        }, executor);
    }
}

