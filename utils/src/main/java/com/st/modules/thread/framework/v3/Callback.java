package com.st.modules.thread.framework.v3;

/** 回调接口：任务完成后通知 */
public interface Callback<T> {
    void onSuccess(AsyncTaskContext ctx, T result);
    void onFailure(AsyncTaskContext ctx, Throwable ex);
}
