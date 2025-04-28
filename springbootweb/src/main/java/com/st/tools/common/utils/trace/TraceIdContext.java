package com.st.tools.common.utils.trace;

import com.st.tools.common.filter.BaseFilter;

/**
 * <li>TraceId 支持</li>
 * 全局唯一 ID ref {@link BaseFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain}
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
