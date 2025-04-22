package com.st.tools.springbootweb.filter;

import com.st.tools.springbootweb.utils.trace.TraceIdContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;


@Slf4j
public class TraceIdFilter implements Filter {

    private static final String HEADER_NAME = "X-Trace-Id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String traceId = request.getHeader(HEADER_NAME);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        TraceIdContext.setTraceId(traceId);

        try {
            chain.doFilter(req, res);
        } finally {
            log.info("traceId:" + TraceIdContext.getTraceId());
            TraceIdContext.clear();
        }
    }


    // TODO 研究mdc
    /**
     * <li>添加 TraceId（通过 Filter + MDC 实现日志链路）</li>
     * <li>并在 logback-spring.xml 中添加 %X{traceId} 输出：</li>
     */
    /*@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        MDC.put("traceId", traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
        }
    }*/
}