package com.st.auto;

/**
 * 概述:
 *  CommonResult 用于封装接口的统一返回结构, 包含状态码、提示信息与数据。
 * 功能清单:
 *  1. 提供成功、失败等静态构造方法。
 *  2. 可用于所有 Web 响应体的标准结构。
 * 使用示例:
 *  return CommonResult.success(userDto);
 * 注意事项:
 *  1. 建议所有接口最终返回该结构, 便于前端统一处理。
 */
public class CommonResult<T> {
    private int code;
    private String message;
    private T data;
    public CommonResult() {
    }
    public CommonResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    /**
     * 入参:
     *  data: 业务数据。
     * 出参:
     *  返回封装为成功状态的 CommonResult。
     * 异常说明:
     *  无异常, 属于纯构造方法。
     */
    public static <T> CommonResult<T> success(T data) {
        // 方法内部注释: 使用通用成功码与默认成功信息
        return new CommonResult<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }
    /**
     * 入参:
     *  errorCode: 错误码枚举。
     * 出参:
     *  返回封装为失败状态的 CommonResult。
     * 异常说明:
     *  若 errorCode 为空可能抛出 NullPointerException, 调用前需保证非空。
     */
    public static <T> CommonResult<T> failure(ErrorCode errorCode) {
        return new CommonResult<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
    // 省略 getter/setter
}

