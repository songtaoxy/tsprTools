package com.st.auto;
/**
 * 概述:
 *  ErrorCode 定义系统通用错误码。
 * 功能清单:
 *  1. 提供成功、通用错误、参数错误等标准错误码。
 * 使用示例:
 *  throw new BizException(ErrorCode.INVALID_PARAM);
 * 注意事项:
 *  1. 实际项目中可根据业务扩展更多枚举值。
 */
public enum ErrorCode {
    SUCCESS(0, "OK"),
    INVALID_PARAM(1001, "参数错误"),
    BUSINESS_ERROR(2000, "业务异常"),
    SYSTEM_ERROR(5000, "系统错误");
    private final int code;
    private final String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
