package com.st.auto;

import com.st.common.aspect.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 概述:
 *  CommonWebAutoConfiguration 负责在 Web 环境中自动装配通用 Web 组件。
 * 功能清单:
 *  1. 注册全局异常处理器。
 *  2. 注册统一响应包装拦截器。
 *  3. 注册 TraceId 过滤器。
 * 使用示例:
 *  Spring Boot 应用只需引入 ai-common-web-starter 依赖, 即可自动生效。
 * 注意事项:
 *  1. 通过 @ConditionalOnClass 等注解控制仅在 Web 环境生效。
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class CommonWebAutoConfiguration {
    /**
     * 入参:
     *  无。
     * 出参:
     *  返回 GlobalExceptionHandler 实例。
     * 异常说明:
     *  无特殊异常。
     */
   /* @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        // 方法内部注释: 全局异常处理器由 Spring 管理为单例 Bean
        return new GlobalExceptionHandler();
    }*/
    /**
     * 入参:
     *  无。
     * 出参:
     *  返回 GlobalResponseBodyAdvice 实例。
     * 异常说明:
     *  无特殊异常。
     */
 /*   @Bean
    public GlobalResponseBodyAdvice globalResponseBodyAdvice() {
        return new GlobalResponseBodyAdvice();
    }*/
    /**
     * 入参:
     *  无。
     * 出参:
     *  返回 TraceIdFilter 实例。
     * 异常说明:
     *  无特殊异常。
     */
   /* @Bean
    public TraceIdFilter traceIdFilter() {
        return new TraceIdFilter();
    }*/
}
