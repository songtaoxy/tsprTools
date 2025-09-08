package com.st.modules.thread.framework.v4.orchestration;

/** 结构化结果：与 names 一一对应 */
public final class ResultItem {
    public final String name;
    public final String value;   // 真实值或 fallback 占位
    public final String status;  // SUCCESS | FALLBACK
    public final String error;   // FALLBACK 时的原因（可选）

    public ResultItem(String name, String value, String status, String error) {
        this.name=name; this.value=value; this.status=status; this.error=error;
    }
    public static ResultItem success(String name, String v){ return new ResultItem(name, v, "SUCCESS", null); }
    public static ResultItem fallback(String name, String v, String err){ return new ResultItem(name, v, "FALLBACK", err); }
}
