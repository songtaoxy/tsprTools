package com.st.tools.springbootweb.filter;

import com.st.tools.springbootweb.i18n.I18nUtil;
import com.st.tools.springbootweb.utils.bean.SpringContextUtils;
import com.st.tools.springbootweb.utils.log.LogHelper;
import com.st.tools.springbootweb.utils.trace.TraceIdContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


/**
 * <li>MDC: 当前filter存值, logback中使用. 日志格式中, 用 %X{key} 输出 MDC 信息</li>
 * <li>哪些基础信息:
 * <ul>traceId</ul>
 * <ul>租户ID</ul>
 * </li>
 */
@Slf4j
@Component
public class BaseFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-Trace-Id";


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // traceId
        String traceId = req.getHeader(HEADER_NAME);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        // tenantId
        String tenantId = req.getHeader("X-Tenant-Id");

        // 请求信息
        String ip = LogHelper.getClientIp(req);
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String uri = req.getRequestURI();
        String method = req.getMethod();

        I18nUtil i18nUtil = SpringContextUtils.getContext().getBean(I18nUtil.class);
        String locale = i18nUtil.getCurrentLocale().getDisplayLanguage();


        // 存放各种信息
        TraceIdContext.setTraceId(traceId);
        MDC.put("traceId", traceId);
        MDC.put("tenantId", tenantId != null ? tenantId : "default");
        MDC.put("ip", ip);
        MDC.put("uri", uri);
        MDC.put("contextPath", contextPath);
        MDC.put("servletPath", servletPath);
        MDC.put("method", method);
        MDC.put("locale", locale);


        try {
            chain.doFilter(req, res);
        } finally {
            log.info("当前线程请求与返回结束后, 线程结束, 清空数据");
            TraceIdContext.clear();
            MDC.remove("traceId");
            MDC.remove("tenantId");
            MDC.remove("ip");
            MDC.remove("uri");
            MDC.remove("contextPath");
            MDC.remove("servletPath");
            MDC.remove("method");
            MDC.remove("locale");
        }
    }



}