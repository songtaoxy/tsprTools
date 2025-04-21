package com.st.tools.springbootweb.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <li>支持Swagger注解, 对Swagger展示</li>
 * @param <T>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用响应结构")
public class Response<T> {

    @Schema(description = "状态码，如 200、500", example = "200")
    private String code;

    @Schema(description = "返回信息，支持国际化", example = "操作成功")
    private String msg;

    @Schema(description = "实际返回结果（业务数据或错误信息）")
    private T result;
}
