package com.st.modules.thread.framework.v3;

/** 可选：指标/埋点回调接口（对接 Micrometer/Prometheus 可实现该接口） */
public interface MetricsSink {
    void onSubmit(String pool, String kind);
    void onComplete(String pool, String kind, boolean success);
    void onRejected(String pool, String reason);
    MetricsSink NOOP = new MetricsSink() {
        public void onSubmit(String p, String k) {}
        public void onComplete(String p, String k, boolean s) {}
        public void onRejected(String p, String r) {}
    };
}

