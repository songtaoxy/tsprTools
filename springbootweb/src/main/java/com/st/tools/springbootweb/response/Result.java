package com.st.tools.springbootweb.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *  * <li>支持Swagger注解, 对Swagger展示</li>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "错误信息结构")
public class Result {

    @Schema(description = "时间戳", example = "2025-04-22T14:22:00")
    private LocalDateTime timestamp;

    @Schema(description = "错误详情", example = "java.lang.NullPointerException")
    private String detail;

    @Schema(description = "请求路径", example = "/api/user/save")
    private String path;

    @Schema(description = "请求追踪ID", example = "3e64c60a-45f2-4e26-a289-b6be72fe95f3")
    private String traceId;

    private String locale;

}
