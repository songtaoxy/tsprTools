package com.st.tools.common.config.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;


/**
 * <li>swagger不同版本, 访问地址
 * <ul>2.9.x 访问地址: http://ip:port/{context-path}/swagger-ui.html</ul>
 * <ul>3.0.x 访问地址: http://ip:port/{context-path}/swagger-ui/index.html</ul>
 * <ul>当前项目访问地址: http://localhost:8080/st/swagger-ui/index.html</ul>
 * <ul>3.0集成knife4j 访问地址: http://ip:port/{context-path}/doc.html</ul>
 * </li>
 */
//@EnableOpenApi
@Configuration
public class SwaggerConfig {

   /* @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30) // OAS_30 = OpenAPI 3.0，兼容 Swagger UI
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.controller")) // 修改为你的包路径
                .paths(PathSelectors.any())
                .build();
    }*/
    /**
     * 配置基本信息
     * @return
     */
/*    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger Test App Restful API")
                .description("swagger test app restful api")
                .termsOfServiceUrl("https://github.com/geekxingyun")
                .contact(new Contact("技术宅星云","https://xingyun.blog.csdn.net","fairy_xingyun@hotmail.com"))
                .version("1.0")
                .build();
    }*/

    /**
     * 配置文档生成最佳实践
     * @param apiInfo
     * @return
     */
/*    @Bean
    public Docket createRestApi(ApiInfo apiInfo) {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo)
                .groupName("SwaggerGroupOneAPI")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }*/

    /**
     * <li>分组接口</li>
     * <li>分组后 Swagger UI 页面会以标签形式分类接口，便于模块化展示</li>
     */
    /*@Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户模块")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("订单模块")
                .pathsToMatch("/api/order/**")
                .build();
    }*/

}
