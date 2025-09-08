package com.st.modules.thread.framework.v4.context;


import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

/** 入站 Filter：自动创建 BizContext + 填充 MDC；响应前清理 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public final class BizContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String traceId = headerOrGen(req, "X-Trace-Id");
        String tenant  = req.getHeader("X-Tenant");
        String user    = req.getHeader("X-User");
        String taskId  = UUID.randomUUID().toString();

        MDC.put("traceId", traceId);
        MDC.put("taskId", taskId);
        if (tenant != null) MDC.put("tenant", tenant);
        if (user != null) MDC.put("user", user);

        BizContext ctx = new BizContext(taskId, traceId, tenant, user);
        try (BizContextHolder.Scope scope = BizContextHolder.with(ctx)) {
            chain.doFilter(req, res);
        } finally {
            BizContextHolder.clear();
            MDC.remove("traceId"); MDC.remove("taskId"); MDC.remove("tenant"); MDC.remove("user");
        }
    }

    private static String headerOrGen(HttpServletRequest req, String name){
        String v = req.getHeader(name);
        return (v == null || v.isEmpty()) ? UUID.randomUUID().toString() : v;
    }
}
