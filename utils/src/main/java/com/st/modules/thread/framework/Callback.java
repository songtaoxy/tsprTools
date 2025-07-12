package com.st.modules.thread.framework;

public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(Throwable ex);
}