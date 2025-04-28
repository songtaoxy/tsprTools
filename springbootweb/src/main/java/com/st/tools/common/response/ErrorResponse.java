package com.st.tools.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 把 Response<ErrorResult> 等作为单独组件显示
 */
@Schema(name = "ErrorResponse", description = "错误响应结构")
public class ErrorResponse extends Response<Result> {
    public ErrorResponse(String code, String msg, Result result) {
        super(code, msg, result);
    }
}
