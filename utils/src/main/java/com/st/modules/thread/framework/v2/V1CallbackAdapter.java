package com.st.modules.thread.framework.v2;


import com.st.modules.thread.framework.v1.Callback;
import com.st.modules.thread.framework.v2.AsyncTaskContext;

/**
 * 概述：回调适配器, 兼容 v1 与 v2 两种回调签名
 * 功能清单：
 * 1）将 v1 回调包装为 v2 风格
 * 2）将 v2 回调降级为 v1 风格（如需）
 * 使用示例：new V1CallbackAdapter(v1Cb).onSuccess(ctx, result)
 * 注意事项：避免在回调中做阻塞操作
 * 入参与出参与异常说明：入参为被适配回调；不抛检查异常
 */
public class V1CallbackAdapter<T> implements com.st.modules.thread.framework.v2.Callback<T> {
    private final Callback<T> v1;
    public V1CallbackAdapter(Callback<T> v1) { this.v1 = v1; }
    public void onSuccess(AsyncTaskContext ctx, T result) { v1.onSuccess(result); }
    public void onFailure(AsyncTaskContext ctx, Throwable ex) { v1.onFailure(ex); }
}
