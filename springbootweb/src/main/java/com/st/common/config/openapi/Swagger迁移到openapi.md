# 问题

引入之后会报错, 不引入则不会报错

```java
  <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

报错: 

```
2025-09-05 10:35:08.832-3619 [ERROR] [ restartedMain] [tenantId:] [locale:] [traceId:] [ip:] [uri:] [contextPath:] [servletPath:] [method:] org.springframework.boot.SpringApplication#reportFailure:821 : [Application run failedorg.springframework.context.ApplicationContextException: Failed to start bean 'documentationPluginsBootstrapper'; nested exception is java.lang.NullPointerException
```

## 解析

这是 Spring Boot 2.6+ 与 Springfox 的经典不兼容点

### 根因与现象

引入 `spring-boot-starter-actuator` 后，Spring 会多注册一个基于 `PathPatternParser` 的 `WebMvcEndpointHandlerMapping`。若项目里仍使用 Springfox（如 3.0.0），其 `documentationPluginsBootstrapper` 在扫描 HandlerMappings 时对该映射兼容性不足，触发 `NullPointerException`。不引入 Actuator 时没有该映射，因此不报错。这是 Spring Boot 2.6+ 与 Springfox 的经典不兼容点

### 两种解决路线

#### 路线A（继续用 Springfox，限 Spring Boot 2.6–2.7）

在不迁移的前提下，需同时做两个动作：回退匹配策略 + 过滤带 PatternParser 的映射

1. 配置回退为 AntPathMatcher

```properties
# application.properties
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

1. 注册补丁 Bean 过滤不兼容的 HandlerMapping（Java 8）

```java
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

@Configuration
public class SpringfoxCompatConfig {
    /**
     * 概述：修复 Springfox 与 Spring Boot 2.6+ 的 HandlerMapping 兼容问题
     * 功能清单：
     * 1. 在容器初始化后，移除带有 PathPatternParser 的 HandlerMapping
     * 2. 避免 Springfox 在文档引导阶段 NPE
     * 使用示例：直接作为配置类被扫描生效
     * 注意事项：
     * 1. 仅适用于 Spring MVC 场景，WebFlux 请改用 Springdoc
     * 2. 配合 spring.mvc.pathmatch.matching-strategy=ant_path_matcher 一起使用
     * 入参：无
     * 出参：BeanPostProcessor
     * 异常：内部吞掉反射异常，不影响启动
     */
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                try {
                    boolean isMvcProvider = bean.getClass().getName().endsWith("WebMvcRequestHandlerProvider");
                    boolean isFluxProvider = bean.getClass().getName().endsWith("WebFluxRequestHandlerProvider");
                    if (!isMvcProvider && !isFluxProvider) return bean;
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    if (field == null) return bean;
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<Object> mappings = (List<Object>) field.get(bean);
                    if (mappings == null) return bean;
                    Iterator<Object> it = mappings.iterator();
                    while (it.hasNext()) {
                        Object mapping = it.next();
                        try {
                            Method m = mapping.getClass().getMethod("getPatternParser");
                            Object parser = m.invoke(mapping);
                            if (parser != null) it.remove();
                        } catch (NoSuchMethodException ignore) {
                            // 老版本没有该方法，忽略
                        }
                    }
                } catch (Throwable ignore) {
                    // 任何反射失败都不影响应用继续启动
                }
                return bean;
            }
        };
    }
}
```

1. 易错点自查

- 不要使用 `@EnableWebMvc`
- `Docket.select().apis(RequestHandlerSelectors.basePackage("..."))` 的包名确保非空
- 项目中不要混入 `spring-boot-starter-webflux`

#### 路线B（推荐：迁移到 Springdoc，适配 Spring Boot 3.x 及以后）

根治方式是移除 Springfox，改用 Springdoc OpenAPI（原生兼容 Actuator 与 PathPattern）

1. 移除 Springfox 相关依赖与配置类
2. 引入 Springdoc（WebMVC 场景）

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version><!-- 示例版本，请与当前 Boot 对齐 -->
</dependency>
```

1. 最小配置即可使用（可选）

```java
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    /**
     * 概述：最小 OpenAPI 元信息
     * 功能清单：设置标题与版本，供 Swagger UI 展示
     * 使用示例：访问 /swagger-ui.html 或 /swagger-ui/index.html
     * 注意事项：无需手写 Docket；与 Actuator 共存不冲突
     * 入参：无
     * 出参：OpenAPI
     * 异常：无
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Demo API").version("v1"));
    }
}
```

### 快速决策建议

- 你当前只是“引入 Actuator 就报错”，且十有八九用了 Springfox
   优先方案：若是 Boot 3.x，直接走路线B迁移 Springdoc
   兜底方案：若是 Boot 2.6–2.7 且暂不迁移，按路线A同时设置属性与补丁 Bean

### 额外排查清单

- 执行 `mvn -q -DforceStdout dependency:tree | grep -i springfox` 确认是否仍存在任意 Springfox 依赖
- 若引入过多 Swagger 相关包（`springfox-boot-starter`、`springfox-swagger2`、`springfox-swagger-ui` 混用），请精简为单一 Starter 或彻底移除
- 确认未引入 `spring-boot-starter-webflux`（与 MVC 混用易放大不兼容）

## springboot版本是2.7.9

### 结论

你用的是 Spring Boot 2.7.9，属于 2.6–2.7 区间；引入 Actuator 后与 Springfox 的 `documentationPluginsBootstrapper` 在 HandlerMapping 处理上不兼容导致 NPE。保留 Springfox 的最小可行修复如下；若可迁移，建议改用 Springdoc 1.x（专为 Boot 2.x）。

### 仍用 Springfox 的最小修复步骤（推荐先做）

1. 在配置中把路径匹配回退为 AntPathMatcher：`spring.mvc.pathmatch.matching-strategy=ant_path_matcher`
2. 加一个 BeanPostProcessor 过滤带 PathPatternParser 的 HandlerMapping（Java8 代码如下）

```java
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

@Configuration
public class SpringfoxCompatConfig {
    /**
     * 概述：修复 Springfox 在 Spring Boot 2.6+ 下与 Actuator 产生的 HandlerMapping 不兼容
     * 功能清单：容器初始化后移除带 PathPatternParser 的映射以避免 NPE
     * 使用示例：作为配置类被扫描后自动生效
     * 注意事项：仅适用于 Spring MVC；不要与 WebFlux 混用
     * 入参：无
     * 出参：BeanPostProcessor
     * 异常：内部吞掉反射异常以确保启动不受影响
     */
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                try {
                    boolean isMvcProvider = bean.getClass().getName().endsWith("WebMvcRequestHandlerProvider");
                    boolean isFluxProvider = bean.getClass().getName().endsWith("WebFluxRequestHandlerProvider");
                    if (!isMvcProvider && !isFluxProvider) return bean;
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    if (field == null) return bean;
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<Object> mappings = (List<Object>) field.get(bean);
                    if (mappings == null) return bean;
                    Iterator<Object> it = mappings.iterator();
                    while (it.hasNext()) {
                        Object mapping = it.next();
                        try {
                            Method m = mapping.getClass().getMethod("getPatternParser");
                            Object parser = m.invoke(mapping);
                            if (parser != null) it.remove();
                        } catch (NoSuchMethodException ignore) {
                        }
                    }
                } catch (Throwable ignore) {
                }
                return bean;
            }
        };
    }
}
```

1. 自查要点

- 不要加 `<b><u>@EnableWebMvc</u></b>`
- `Docket.select().apis(RequestHandlerSelectors.basePackage("..."))` 的包名务必非空
- 仅使用 MVC：确保未引入 `spring-boot-starter-webflux`
- Springfox 建议精简为 `springfox-boot-starter:3.0.0` 单一依赖，避免多包混用

### 可选的根治方案：迁移到 Springdoc 1.x（适配 Boot 2.7）

1. 移除所有 Springfox 依赖与配置类
2. 引入

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-ui</artifactId>
  <version>1.7.0</version>
</dependency>
```

1. 启动后访问 `/swagger-ui.html` 或 `/swagger-ui/index.html`；OpenAPI JSON 在 `/v3/api-docs`
2. 可选最小配置（Java8）

```java
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    /**
     * 概述：最小 OpenAPI 元信息
     * 功能清单：设置标题与版本
     * 使用示例：访问 swagger UI 即可
     * 注意事项：与 Actuator 原生兼容
     * 入参：无
     * 出参：OpenAPI
     * 异常：无
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Demo API").version("v1"));
    }
}
```

### 为什么引入 Actuator 就出错

Actuator 在 MVC 下会注册 `WebMvcEndpointHandlerMapping` 等使用 PathPatternParser 的映射；Springfox 3.0.0 在扫描这些映射时未做充分空值与类型兼容判断，因而触发 `NullPointerException`。上述两步修复的本质就是让 Springfox “看不到”这些基于新解析器的映射，或直接改用 Springdoc 规避该类不兼容。



# swagger相关的配置, 如EnableOpenApi, ApiInfo没法用了, 怎么办



### 结论

迁到 Springdoc 后，不再需要 `@EnableOpenApi`，也不再使用 `springfox.documentation.service.ApiInfo`。等价能力由两种方式提供：其一是在类上用 `@OpenAPIDefinition` 注解声明元信息；其二是注册一个 `OpenAPI` Bean 并填充 `io.swagger.v3.oas.models.info.Info`、`Contact`、`License` 等对象。分组与扫描用 `GroupedOpenApi` 取代 `Docket`。官方迁移指引与示例即是这样做的。([OpenAPI 3 Library for spring-boot](https://springdoc.org/v1/migrating-from-springfox.html?utm_source=chatgpt.com), [GitHub](https://github.com/springdoc/springdoc-openapi?utm_source=chatgpt.com))

### ApiInfo 等价配置（推荐方式一：OpenAPI Bean）

```java
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
```

上述即 Springdoc 的“ApiInfo”。`OpenAPI` Bean 或 `@OpenAPIDefinition` 都会被自动扫描用于生成文档。([GitHub](https://github.com/springdoc/springdoc-openapi?utm_source=chatgpt.com))

### ApiInfo 等价配置（方式二：类级注解）

```java
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Demo API",
        version = "v1.0.0",
        description = "示例接口文档",
        contact = @Contact(name = "Team A", email = "team@example.com", url = "https://example.com"),
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
    )
)
public class OpenApiAnnoConfig {
}
```

### 分组与扫描（替代 Docket）

```java
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupConfig {
    /**
     * 概述：以分组替代 Docket
     * 功能清单：按包扫描 按路径匹配 便于模块化
     * 使用示例：访问 /v3/api-docs/public 与 /v3/api-docs/admin
     * 注意事项：packagesToScan 与 pathsToMatch 可组合使用
     * 入参：无
     * 出参：GroupedOpenApi
     * 异常：无
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .packagesToScan("com.example.app.publicapi")
            .pathsToMatch("/public/**")
            .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("admin")
            .packagesToScan("com.example.app.admin")
            .pathsToMatch("/admin/**")
            .build();
    }
}
```

`GroupedOpenApi` 是 Springdoc 对标 Springfox `Docket` 的官方做法。([OpenAPI 3 Library for spring-boot](https://springdoc.org/migrating-from-springfox.html?utm_source=chatgpt.com))

### 注解替换清单（控制器与模型）

- `@Api`→`@Tag`；`@ApiOperation`→`@Operation`; `@ApiParam`/`@ApiImplicitParam`→`@Parameter`; `@ApiResponses`/`@ApiResponse` 仍在但包名改为 `io.swagger.v3.oas.annotations.*`；模型上的 `@ApiModel`/`@ApiModelProperty`→`@Schema`；跳过文档的 `@ApiIgnore`→`@Hidden`。([Stack Overflow](https://stackoverflow.com/questions/59291371/migrating-from-springfox-swagger-2-to-springdoc-open-api?utm_source=chatgpt.com))

```java
// Springfox:
// @Api(tags = "User")
// @ApiOperation("Get user by id")
// public User get(@ApiParam("用户ID") @PathVariable Long id)

// Springdoc:
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "User")
@Operation(summary = "Get user by id")
public User get(@Parameter(description = "用户ID") @PathVariable Long id) { ... }
```

### （可选）安全定义与应用示例

```java
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemeType;

// 声明一个名为 bearerAuth 的全局安全方案
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@Configuration
public class OpenApiSecurityConfig {}

// 在控制器或方法上声明使用
@Operation(summary = "受保护的接口")
@SecurityRequirement(name = "bearerAuth")
public User me() { ... }
```

上面等价于 Springfox 的 `ApiKey`/`Authorization` 配置方式。([Baeldung on Kotlin](https://www.baeldung.com/spring-rest-openapi-documentation?utm_source=chatgpt.com))

### 常用开关与访问路径

- 依赖：`org.springdoc:springdoc-openapi-ui:1.7.0`（适配 Boot 2.7.x）([Maven Repository](https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-ui?utm_source=chatgpt.com))
- 访问：`/swagger-ui.html` 与 `/v3/api-docs`；若启用 Actuator，还可通过管理端口下的 `actuator/swagger-ui` 与 `actuator/openapi`。([HelpEzee](https://helpezee.wordpress.com/2022/07/07/failed-to-start-bean-documentationpluginsbootstrapper-use-springdoc-openapi-ui/?utm_source=chatgpt.com), [OpenAPI 3 Library for spring-boot](https://springdoc.org/?utm_source=chatgpt.com))
- 关闭开关（生产环境建议）：`springdoc.api-docs.enabled=false`、`springdoc.swagger-ui.enabled=false`（按需启停，避免暴露）

### 要点小结

- 不要再使用 `@EnableOpenApi` 与 `springfox.documentation.*` 类型；用 `@OpenAPIDefinition` 或 `OpenAPI` Bean + `GroupedOpenApi` 完成同等配置。([OpenAPI 3 Library for spring-boot](https://springdoc.org/v1/migrating-from-springfox.html?utm_source=chatgpt.com))
- Springdoc 会自动根据 Spring Boot 的组件扫描范围发现控制器；若你的控制器在默认扫描包外，请补充组件扫描或用 `packagesToScan` 指定。([Stack Overflow](https://stackoverflow.com/questions/59921858/springdoc-openapi-tool-not-scanning-for-apis-in-spring-boot-project?utm_source=chatgpt.com))
   如果你把现有 Springfox 配置发来（Docket、ApiInfo、分组、安保设置），我可以一对一改写成 Springdoc 等价版本，确保 2.7.9 + Actuator 下稳定可用。

# @OpenAPIDefinition 用法

### 核心认识

`@OpenAPIDefinition` 是 Springdoc 提供的 OpenAPI 元信息注解入口，用来在一个地方声明标题、版本、描述、服务器地址、外部文档、全局安全需求与标签等。它是对旧版 Springfox `@EnableOpenApi + ApiInfo` 的等价替代之一，常加在 `@Configuration` 或启动类上

### 典型用法模板（适配 Spring Boot 2.7.x 与 springdoc-openapi 1.7.x）

```java
package com.example.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * 概述：集中声明 OpenAPI 元信息，替代 Springfox 的 EnableOpenApi 与 ApiInfo
 * 功能清单：
 * 1. 标题/版本/描述/联系人/许可证
 * 2. Servers 声明（网关或 context-path 场景）
 * 3. 外部文档 ExternalDocs
 * 4. 全局安全需求 SecurityRequirement（与 @SecurityScheme 配合）
 * 5. 预声明常用标签 Tag
 * 使用示例：启动后访问 /swagger-ui.html 或 /v3/api-docs
 * 注意事项：
 * 1. 推荐全局只放一个该注解，避免多处冲突
 * 2. 细粒度分组请用 GroupedOpenApi；此注解仅放全局元信息
 * 入参与出参与异常说明：配置注解，无入参与出参，无运行时异常
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Demo API",
        version = "v1.0.0",
        description = "示例接口文档说明",
        contact = @Contact(name = "Team A", email = "team@example.com", url = "https://example.com"),
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
    ),
    servers = {
        @Server(url = "/", description = "默认服务端点"),
        @Server(url = "https://api.example.com", description = "生产网关")
    },
    externalDocs = @ExternalDocumentation(
        description = "更多参考",
        url = "https://example.com/docs"
    ),
    security = {
        @SecurityRequirement(name = "bearerAuth")
    },
    tags = {
        @Tag(name = "User", description = "用户相关接口"),
        @Tag(name = "Admin", description = "管理端接口")
    }
)
public class OpenApiDefinitionConfig {
}
```

### 与 OpenAPI Bean 的关系

- 二选一或两者并存：`@OpenAPIDefinition` 适合声明“静态元信息”；`@Bean OpenAPI` 适合“运行时动态拼装”（如读取配置中心开关、多语言）
- 并存时的合并：Springdoc 会合并两处信息；相同字段以更具体者覆盖。为避免歧义，建议将“固定信息”放注解，动态信息放 `OpenAPI` Bean

```java
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * 概述：基于配置的动态版本号覆盖
 * 功能清单：从环境变量覆盖版本
 * 使用示例：与 @OpenAPIDefinition 并存
 * 注意事项：字段重复时以此 Bean 的值为准
 * 入参与出参与异常说明：无入参，返回 OpenAPI，无异常
 */
@Bean
public OpenAPI dynamicOpenAPI(org.springframework.core.env.Environment env) {
    String ver = env.getProperty("app.version", "v1.0.0");
    return new OpenAPI().info(new Info().title("Demo API").version(ver));
}
```

### 与分组 GroupedOpenApi 的联动（替代 Docket）

`@OpenAPIDefinition` 放全局元信息；“分组与扫描范围”用 `GroupedOpenApi` 完成

```java
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 概述：以分组实现按包与路径出文档
 * 功能清单：public 与 admin 两组
 * 使用示例：/v3/api-docs/public 与 /v3/api-docs/admin
 * 注意事项：packagesToScan 与 pathsToMatch 可组合
 * 入参与出参与异常说明：无入参，返回 GroupedOpenApi，无异常
 */
@Configuration
public class OpenApiGroupConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .packagesToScan("com.example.api.publics")
            .pathsToMatch("/public/**")
            .build();
    }
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("admin")
            .packagesToScan("com.example.api.admin")
            .pathsToMatch("/admin/**")
            .build();
    }
}
```

### 配合安全定义使用的完整示例（JWT Bearer）

```java
package com.example.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemeType;

/**
 * 概述：声明全局安全方案并与 @OpenAPIDefinition 的 SecurityRequirement 对齐
 * 功能清单：注册名为 bearerAuth 的 HTTP Bearer 方案
 * 使用示例：在控制器或全局使用 @SecurityRequirement(name = "bearerAuth")
 * 注意事项：方案名需与 @OpenAPIDefinition.security 中的 name 完全一致
 * 入参与出参与异常说明：配置注解，无入参与出参与异常
 */
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiSecurityConfig {
}
```

### 多环境与网关场景的服务器地址声明

- 网关前置：在 `servers` 中写网关地址，如 `https://gw.example.com`，并确保下游服务控制器的 `@RequestMapping` 只描述相对路径
- context-path：若应用配置了 `server.servlet.context-path=/app`，可以把 `@Server(url = "/app")` 加入 servers；或完全省略 servers 让 Springdoc 自动感知
- 动态环境：如需按环境切换 servers，可去掉注解中的 servers，改用 `OpenAPI` Bean 读取 `spring.profiles.active` 拼装

### 常见坑与规约

- 只放一个全局 `@OpenAPIDefinition` 更清晰；多处声明会被合并但增大歧义
- `tags` 在此处只是“预声明元信息”，实际接口仍以方法上的 `@Tag` 为准
- 与 Actuator 共存无需特殊处理；文档路径仍是 `/swagger-ui.html` 与 `/v3/api-docs`
- 不要再使用 Springfox 的 `@EnableOpenApi`、`ApiInfo`、`Docket`；等价功能分别由 `@OpenAPIDefinition`、`OpenAPI` Bean、`GroupedOpenApi` 提供
- 若你使用的是 Springdoc 2.x（适配 Boot 3），注解用法一致；依赖与路径略有差异但不影响此注解

### 最小落地步骤清单

- 在一个 `@Configuration` 类上加 `@OpenAPIDefinition(info=@Info(...))`
- 如需分组，新增 `GroupedOpenApi` Bean
- 如需鉴权，在单独配置类上加 `@SecurityScheme`，并在 `@OpenAPIDefinition.security` 或控制器方法上使用 `@SecurityRequirement`
- 启动验证 `/swagger-ui.html`、`/v3/api-docs` 是否符合预期

