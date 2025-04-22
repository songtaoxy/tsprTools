package com.st.tools.springbootweb.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 把 Response<ErrorResult> 等作为单独组件显示
 */
@Schema(name = "ErrorResponse", description = "错误响应结构")
public class ErrorResponse extends Response<ErrorResult> {
    public ErrorResponse(String code, String msg, ErrorResult result) {
        super(code, msg, result);
    }
}
