package com.st.tools.common.interceptor;

import com.st.tools.common.utils.log.LogHelper;
import com.st.tools.common.utils.trace.TraceIdContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <li>记录请求耗时的拦截器. 请求耗时功能, 已经有更好方案, ref
 * {@code com.st.tools.springbootweb.filter.BaseFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}</li>
 * <pre>
 *     类似于spring提供的org.springframework.web.filter.CommonsRequestLoggingFilter
 * </pre>
 *
 */
@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "start-time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());


        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = LogHelper.getClientIp(request);
        String traceId = LogHelper.getTraceId();

        String tenantId = MDC.get("tenantId");

        log.info("Method: [{}] - URL: {} - IP: {} - TraceId: {} - TenandId: {}", method, uri, ip, traceId,tenantId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long start = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - start;

//        log.info("[{}] {} - Done in {} ms", request.getMethod(), request.getRequestURI(), cost);

        log.info("TenantId={},TraceId={} 请求={} 耗时={}ms",MDC.get("tenantId"), TraceIdContext.getTraceId(), request.getRequestURI(), duration);
    }
}
