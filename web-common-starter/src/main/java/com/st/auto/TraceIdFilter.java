package com.st.auto;
import org.slf4j.MDC;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
/**
 * 概述:
 *  TraceIdFilter 为每个请求生成或透传 TraceId, 并放入 MDC 以便日志链路追踪。
 * 功能清单:
 *  1. 从请求头中读取 TraceId, 若不存在则生成新的。
 *  2. 将 TraceId 设置进 MDC。
 * 使用示例:
 *  引入 ai-common-web-starter 后, 日志中可使用 %X{traceId} 输出链路标识。
 * 注意事项:
 *  1. 请求结束后必须清理 MDC, 避免线程复用导致数据串联。
 */
public class TraceIdFilter implements Filter {
    public static final String TRACE_ID_KEY = "traceId";
    public static final String HEADER_NAME = "X-Trace-Id";
    /**
     * 入参:
     *  request: 请求对象。
     *  response: 响应对象。
     *  chain: 过滤器链。
     * 出参:
     *  无。
     * 异常说明:
     *  可能抛出 IOException 或 ServletException, 由容器处理。
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 方法内部注释: 优先从请求头获取 TraceId, 若无则生成新值
        String traceId = httpRequest.getHeader(HEADER_NAME);
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(TRACE_ID_KEY, traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            // 方法内部注释: 请求处理完毕后清理 MDC 中的 TraceId, 避免线程复用污染
            MDC.remove(TRACE_ID_KEY);
        }
    }
}

