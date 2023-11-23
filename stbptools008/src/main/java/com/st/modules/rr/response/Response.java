package com.st.modules.rr.response;

import com.st.modules.alibaba.fastjson.v1_2_76.FastJsonUtil;

import java.util.HashMap;

/**
 * @Description 基础响应对象
 */
public class Response<T> {
    // 状态码
    public String code;

    // 返回信息
    public String msg;

    // 返回结果
    public T result;

    public Response(String code, String msg, T result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getResult() {
        return result;
    }

    public static <T> Response<T> success() {
        return build(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), null);
    }

    public static <T> Response<T> success(T result) {
        return build(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), result);
    }

    public static <T> Response<T> success(String msg, T result) {
        return build(ResponseEnum.SUCCESS.getCode(), msg, result);
    }

    public static <T> Response<T> fail() {
        return build(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName(), null);
    }

    public static <T> Response<T> fail(String msg) {
        return build(ResponseEnum.FAIL.getCode(), msg, null);
    }

    public static <T> Response<T> fail(String msg, T result) {
        return build(ResponseEnum.FAIL.getCode(), msg, result);
    }

    public static <T> Response<T> custom(ResponseEnum responseCodeEnum, T result) {
        return build(responseCodeEnum.getCode(), responseCodeEnum.getName(), result);
    }

    public static <T> Response<T> build(String code, String massage, T result) {
        // 格式规范化
        if (result == null){
            return new Response(code, massage, new HashMap());
        }
        return new Response(code, massage, result);
    }


    public static <T> String format(Response<T> response){
       String resStr = null;

        resStr = FastJsonUtil.format(response);

        return resStr;
    }

    public static <T> boolean check(Response<T> response){
        boolean validate = false;

        String code200 = ResponseEnum.SUCCESS.getCode();
        String codeRes = response.getCode();

        if(code200.equalsIgnoreCase(codeRes)) {
            validate = true;
        }

        return validate;
    }
}
