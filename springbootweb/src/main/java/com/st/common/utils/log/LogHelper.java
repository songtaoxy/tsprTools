package com.st.common.utils.log;

import com.st.common.utils.trace.TraceIdContext;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * <li>请求基本信息
 * <ul>ip</ul>
 * <ul>url</ul>
 * <ul>链路追踪 traceId</ul>
 * </li>
 */
public class LogHelper {
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    public static String getTraceId() {

        return Optional.ofNullable(TraceIdContext.getTraceId()).orElse("N/A");

//        return Optional.ofNullable(MDC.get("traceId")).orElse("N/A");
    }
}
