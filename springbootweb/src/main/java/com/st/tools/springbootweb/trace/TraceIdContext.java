package com.st.tools.springbootweb.trace;

/**
 * <li>TraceId 支持</li>
 * 全局唯一 ID ref {@link com.st.tools.springbootweb.filter.TraceIdFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain}
 */
public class TraceIdContext {
    private static final ThreadLocal<String> TRACE_ID_HOLDER = new ThreadLocal<>();

    public static void setTraceId(String traceId) {
        TRACE_ID_HOLDER.set(traceId);
    }

    public static String getTraceId() {
        return TRACE_ID_HOLDER.get();
    }

    public static void clear() {
        TRACE_ID_HOLDER.remove();
    }
}
