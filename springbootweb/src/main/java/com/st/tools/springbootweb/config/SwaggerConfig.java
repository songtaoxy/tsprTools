package com.st.tools.springbootweb.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("基础异常响应模块")
                        .version("1.0")
                        .description("提供统一的异常封装结构、国际化、TraceId 追踪日志等功能"));
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .build();
    }
}
