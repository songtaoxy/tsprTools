package com.st.modules.thread.framework.v2;


/**
 * 概述：任务回调接口
 * 功能清单：onSuccess 与 onFailure
 * 使用示例：callback.onSuccess(ctx, result)
 * 注意事项：回调逻辑避免阻塞
 * 入参与出参与异常说明：入参上下文与结果/异常；不抛检查异常
 */
public interface Callback<T> {
    void onSuccess(AsyncTaskContext ctx, T result);
    void onFailure(AsyncTaskContext ctx, Throwable ex);
}
