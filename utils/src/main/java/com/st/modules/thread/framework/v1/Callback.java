package com.st.modules.thread.framework.v1;

public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(Throwable ex);
}