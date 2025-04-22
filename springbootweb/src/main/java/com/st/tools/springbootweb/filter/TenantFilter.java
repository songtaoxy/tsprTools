package com.st.tools.springbootweb.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <li>多租户请求日志隔离（基于 MDC + Header）</li>
 * <li>实现方式: 在日志中加入 tenantId 字段，从请求头自动提取</li>
 * <li>日志输出增加:[%X{tenantId}] 便于日志平台根据租户筛选 {@code <pattern>%d [%thread] %-5level %logger{36} - [Tenant:%X{tenantId}] [TraceId:%X{traceId}] %msg%n</pattern>}</li>
 */
@Component
public class TenantFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String tenantId = request.getHeader("X-Tenant-Id");
        MDC.put("tenantId", tenantId != null ? tenantId : "default");
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove("tenantId");
        }
    }
}
