package com.st.tools.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.time.LocalDateTime;

/**
 *  * <li>支持Swagger注解, 对Swagger展示</li>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "错误信息结构")
public class Result<T> {


    // 存放各种信息
    @Schema(description = "错误详情", example = "java.lang.NullPointerException")
    private T ext;

    // 基本信息
    private Base base;


    public static <R> Result<R> build(R ext){

        Base base = Base.builder()
                .timestamp(LocalDateTime.now())
                .traceId(MDC.get("traceId"))
                .tenantId(MDC.get("tenantId"))
                .locale(MDC.get("locale"))
                .ip(MDC.get("ip"))
                .uri(MDC.get("uri"))
                .contextPath(MDC.get("contextPath"))
                .servletPath(MDC.get("servletPath"))
                .method(MDC.get("method"))
                .build();

        Result<R> result = new Result<R>();
        result.setExt(ext);
        result.setBase(base);

        return result;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Base{
        @Schema(description = "时间戳", example = "2025-04-22T14:22:00")
        private LocalDateTime timestamp;

        @Schema(description = "请求追踪ID", example = "3e64c60a-45f2-4e26-a289-b6be72fe95f3")
        private String traceId;
        private String tenantId;
        private String locale;
        private String ip;
        private String uri;
        private String contextPath;
        private String servletPath;
        private String method;
    }

}
