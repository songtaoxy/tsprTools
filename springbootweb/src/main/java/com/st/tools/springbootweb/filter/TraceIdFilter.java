package com.st.tools.springbootweb.filter;

import com.st.modules.json.jackson.JacksonUtils;
import com.st.tools.springbootweb.trace.TraceIdContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;

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
}