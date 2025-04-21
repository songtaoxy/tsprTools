package com.st.tools.springbootweb.interceptor;

import com.st.tools.springbootweb.trace.TraceIdContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 记录请求耗时的拦截器
 */
@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "start-time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long start = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - start;
        log.info("TraceId={} 请求={} 耗时={}ms", TraceIdContext.getTraceId(), request.getRequestURI(), duration);
    }
}
