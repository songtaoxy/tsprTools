package com.st.auto;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
/**
 * 概述:
 *  GlobalResponseBodyAdvice 对控制层返回结果进行统一封装。
 * 功能清单:
 *  1. 如果返回类型不是 CommonResult, 则自动包装为 CommonResult.success。
 * 使用示例:
 *  Controller 直接返回对象 UserDto, 实际输出为 CommonResult<UserDto>。
 * 注意事项:
 *  1. 可通过自定义注解跳过某些接口的包装。
 */
@RestControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 入参:
     *  returnType: 方法返回类型信息。
     *  converterType: 消息转换器类型。
     * 出参:
     *  返回是否执行 beforeBodyWrite。
     * 异常说明:
     *  无特殊异常。
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 方法内部注释: 若返回类型本身已是 CommonResult 则不再二次封装
        return !CommonResult.class.isAssignableFrom(returnType.getParameterType());
    }
    /**
     * 入参:
     *  body: 控制层原始返回对象。
     *  returnType: 方法返回类型。
     *  selectedContentType: 选定的内容类型。
     *  selectedConverterType: 使用的转换器类型。
     *  request: 请求对象。
     *  response: 响应对象。
     * 出参:
     *  返回最终响应体对象。
     * 异常说明:
     *  无特殊异常。
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 方法内部注释: 将非 CommonResult 响应包装为成功状态的 CommonResult
        return CommonResult.success(body);
    }
}

