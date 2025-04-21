package com.st.tools.springbootweb.aspect;

import com.st.tools.springbootweb.response.Response;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <li>Swagger</li>
 * <li>添加一个注解类，给所有接口和异常定义标准响应结构。</li>
 * <li>所有 RestController 都自动具备统一错误返回文档</li>
 */
@RestControllerAdvice
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "请求参数错误",
                content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误",
                content = @Content(schema = @Schema(implementation = Response.class)))
})
public class GlobalApiResponseAdvice {
}
