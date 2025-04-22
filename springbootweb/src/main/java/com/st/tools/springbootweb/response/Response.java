package com.st.tools.springbootweb.response;

import com.st.modules.json.jackson.JacksonUtils;
import com.st.tools.springbootweb.utils.trace.TraceIdContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;


/**
 * <li>支持Swagger注解, 对Swagger展示</li>
 * @param <T>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Response", description = "统一响应结构")
public class Response<T> {

    @Schema(description = "状态码，如 200、500", example = "200")
    private String code;

    @Schema(description = "返回信息，支持国际化", example = "操作成功")
    private String msg;

    @Schema(description = "实际返回结果（业务数据或错误信息）")
    private T result;


    public static <T> Response<T> build(String code, String massage, T result) {
        // 格式规范化
        if (result == null) {
            return new Response(code, massage, new HashMap());
        }
        return new Response(code, massage, result);
    }

    public static <T> Response<T> res(StatCode statCode, T result) {
        return build(statCode.getCode(), statCode.getName(), result);
    }

    // local是从哪里获取的?
    private static Response<Result> buildRes(StatCode statCode, String detail, String path) {
        Result result = Result.builder()
                .timestamp(LocalDateTime.now())
                .detail(detail)
                .path(path)
                .traceId(TraceIdContext.getTraceId())
                .build();
        return Response.res(statCode, result);
    }

    public static <T> Response<T> custum(String code, String message, T result) {
        return build(code, message, result);
    }


    public Response ok() {
        return build(StatCode.SUCCESS.getCode(), StatCode.SUCCESS.getName(), null);
    }

    public static <T> Response<T> ok(T t) {
     return build(StatCode.SUCCESS.getCode(), StatCode.SUCCESS.getName(), t);
    }

    public static <T> Response<T> ok(String msg, T result) {
        return build(StatCode.SUCCESS.getCode(), msg, result);
    }


    public static Response fail() {
        return build(StatCode.FAIL.getCode(), StatCode.FAIL.getName(), null);
    }


    public static <T> Response<T> fail(T t) {
        return build(StatCode.FAIL.getCode(), StatCode.FAIL.getName(), t);
    }

    public static <T> Response<T> fail(String msg, T result) {
        return build(StatCode.FAIL.getCode(), msg, result);
    }


    public static <T> String format(Response<T> response) {
        String resStr = null;
        resStr = JacksonUtils.toPrettyJson(response);
        return resStr;
    }

    public static <T> boolean check(Response<T> response) {
        boolean validate = false;

        String code200 = StatCode.SUCCESS.getCode();
        String codeRes = response.getCode();

        if (code200.equalsIgnoreCase(codeRes)) {
            validate = true;
        }
        return validate;
    }

    }

