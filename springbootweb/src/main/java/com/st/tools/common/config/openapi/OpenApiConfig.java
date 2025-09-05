package com.st.tools.common.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    /**
     * 概述：替代 Springfox 的 ApiInfo 配置
     * 功能清单：标题 版本 描述 联系人 许可证 外部文档
     * 使用示例：启动后访问 /swagger-ui.html
     * 注意事项：适配 Spring Boot 2.7 与 springdoc-openapi-ui 1.7.x
     * 入参：无
     * 出参：OpenAPI
     * 异常：无
     */
    @Bean
    public OpenAPI projectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo API")
                        .version("v1.0.0")
                        .description("示例接口文档")
                        .contact(new Contact().name("Team A").email("team@example.com").url("https://example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("参考文档")
                        .url("https://example.com/docs"));
    }
}


