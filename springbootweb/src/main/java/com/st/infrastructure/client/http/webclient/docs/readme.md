## 需求
AI 架构中, Java → Python 服务, 用 WebClient + Reactor 写一套企业级主流的 工具类或框架, 可直接使用

## 方案

#### 一、整体概述

这里给你一套在 AI 架构中，Java → Python 服务调用的企业级 WebClient + Reactor 通信层小框架，特点如下：
1 可直接在 Spring Boot 项目中使用（Java 8+，Spring WebFlux）
2 面向 AI 场景：支持普通 JSON 调用和流式（SSE）调用
3 统一封装超时、连接池、错误处理、traceId 透传
4 工具类风格，但结构上可以逐步演进成独立 SDK
下面所有代码均为 Java（JDK8），并且对核心方法给出：概述、功能清单、使用示例、注意事项、入参、出参、异常说明。

#### 二、整体设计说明

1 使用 Spring WebFlux 的 WebClient 作为 HTTP 客户端
2 使用 Reactor Netty HttpClient 做连接池、超时和底层配置
3 通过 Bean 方式提供一个带基础配置的 WebClient（全局复用）
4 封装 PythonServiceClient：

* getJson：GET JSON 请求
* postJson：POST JSON 请求
* postJsonStream：POST JSON 并以 SSE/文本流方式返回（适合 LLM 流式）
  5 使用自定义异常 PythonServiceException 统一错误信息
  6 支持传入 serviceName / path 方便以后接入注册中心或网关

#### 三、依赖说明（Maven）

1 spring-boot-starter-webflux
2 reactor-netty
3 jackson-databind（通常由 Spring Boot 自动引入）
示例：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

#### 四、核心代码一：WebClient 配置（连接池 + 超时）

```java
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 概述:
 *  WebClient 全局配置类, 提供统一的 WebClient Bean, 使用 Reactor Netty 作为底层 HTTP Client.
 * 功能清单:
 *  1. 配置连接池(最大连接数、空闲时间).
 *  2. 配置连接超时、读写超时.
 *  3. 配置默认的编解码策略(例如允许较大的响应体).
 * 注意事项:
 *  1. 建议全局只创建一个 WebClient Bean 并复用.
 *  2. 根据实际 QPS 调整连接池大小和超时配置.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient aiWebClient() {
        // 连接池配置: 名称 + 最大连接数 + 空闲时间等
        ConnectionProvider provider = ConnectionProvider.builder("ai-http-pool")
                .maxConnections(200)                 // 最大连接数, 可按压测结果调整
                .pendingAcquireMaxCount(500)        // 队列最大等待请求数
                .maxIdleTime(Duration.ofSeconds(30))// 连接最大空闲时间
                .build();

        // 底层 HttpClient 配置
        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // 连接超时
                .responseTimeout(Duration.ofSeconds(10))            // 响应超时
                .doOnConnected(conn -> conn
                        // 读写超时处理
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS))
                );

        // 扩大单次响应最大内存, AI 场景经常返回较大 JSON
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024) // 16MB
                )
                .build();

        // 构建 WebClient
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                // 可在此设置一些全局默认 Header, 如 User-Agent 等
                .build();
    }
}
```

#### 五、核心代码二：统一异常类

```java
/**
 * 概述:
 *  Java 调用 Python 服务过程中发生的统一异常类型.
 * 功能清单:
 *  1. 携带 serviceName、path、HTTP 状态码、响应体摘要等信息.
 *  2. 方便上层日志打点和统一处理.
 * 入参:
 *  serviceName: 下游 Python 服务名称或标识.
 *  path: 调用的具体路径.
 *  statusCode: HTTP 状态码, 可能为 null(如网络层异常).
 *  responseBody: 响应体(可选, 建议截断保存).
 *  cause: 原始异常(可选).
 * 出参:
 *  异常对象本身.
 * 异常说明:
 *  上层捕获此异常, 可根据 statusCode 和业务约定进行降级或告警.
 */
public class PythonServiceException extends RuntimeException {

    private final String serviceName;
    private final String path;
    private final Integer statusCode;
    private final String responseBody;

    public PythonServiceException(String message,
                                  String serviceName,
                                  String path,
                                  Integer statusCode,
                                  String responseBody,
                                  Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.path = path;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPath() {
        return path;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
```

#### 六、核心代码三：Python 服务调用客户端

说明
1 支持 GET JSON、POST JSON
2 支持流式 POST（适合 LLM SSE 或文本流）
3 用 serviceBaseUrl 直接传基础地址（你可以扩展为从配置中心或注册中心获取）

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * 概述:
 *  Java -> Python 服务调用的统一客户端, 基于 WebClient + Reactor.
 * 功能清单:
 *  1. getJson: GET JSON 请求, 返回 Mono<T>.
 *  2. postJson: POST JSON 请求, 返回 Mono<T>.
 *  3. postJsonStream: POST JSON + 流式返回, 返回 Flux<String>.
 * 入参约定:
 *  serviceBaseUrl: Python 服务基础地址, 如 "http://python-ai-service:8000".
 *  path: 业务路径, 如 "/api/v1/embedding".
 *  headers: 额外 Header, 如 traceId、authToken 等, 可为 null.
 *  queryParams: GET 查询参数, 可为 null.
 *  requestBody: POST 请求体对象, 会由 Jackson 自动序列化为 JSON.
 *  responseType: 响应类型 Class, 如 MyResponse.class.
 * 出参约定:
 *  getJson/postJson: 返回 Mono<T>, 上层可选择 block 或继续链式操作.
 *  postJsonStream: 返回 Flux<String>, 每个元素可理解为一段流式文本片段.
 * 异常说明:
 *  1. HTTP 非 2xx 状态码会转换为 PythonServiceException.
 *  2. 网络异常、超时等也会包装为 PythonServiceException.
 * 注意事项:
 *  1. 建议所有调用都带 traceId 等链路信息, 便于排查.
 *  2. AI 场景下, 注意响应体大小和超时时间.
 */
@Component
public class PythonServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PythonServiceClient.class);

    private final WebClient webClient;

    public PythonServiceClient(WebClient aiWebClient) {
        this.webClient = aiWebClient;
    }

    /**
     * 概述:
     *  以 GET 方式调用 Python 服务, 期望返回 JSON 并反序列化为指定类型.
     */
    public <T> Mono<T> getJson(String serviceBaseUrl,
                               String path,
                               Map<String, String> queryParams,
                               Map<String, String> headers,
                               Class<T> responseType) {
        String url = serviceBaseUrl + path;

        WebClient.RequestHeadersUriSpec<?> spec = webClient.get();
        WebClient.RequestHeadersSpec<?> requestSpec = spec.uri(uriBuilder -> {
            // 构造 URI: 基础地址 + 路径 + 查询参数
            uriBuilder = uriBuilder.scheme(extractScheme(serviceBaseUrl))
                    .host(extractHost(serviceBaseUrl))
                    .port(extractPort(serviceBaseUrl))
                    .path(path);
            if (!CollectionUtils.isEmpty(queryParams)) {
                queryParams.forEach(uriBuilder::queryParam);
            }
            return uriBuilder.build();
        });

        if (!CollectionUtils.isEmpty(headers)) {
            requestSpec = requestSpec.headers(httpHeaders -> httpHeaders.setAll(headers));
        }

        // 设置接收的媒体类型为 JSON
        requestSpec = requestSpec.accept(MediaType.APPLICATION_JSON);

        // 执行请求
        return requestSpec
                .retrieve()
                // 先统一错误处理
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> handleErrorResponse("GET", serviceBaseUrl, path, response))
                .bodyToMono(responseType)
                // 针对本次调用设置一个整体超时时间(可按场景微调)
                .timeout(Duration.ofSeconds(10))
                .doOnError(e -> log.error("GET 调用 Python 服务异常, url={}", url, e));
    }

    /**
     * 概述:
     *  以 POST JSON 方式调用 Python 服务, 期望返回 JSON 并反序列化为指定类型.
     */
    public <T> Mono<T> postJson(String serviceBaseUrl,
                                String path,
                                Object requestBody,
                                Map<String, String> headers,
                                Class<T> responseType) {
        String url = serviceBaseUrl + path;

        WebClient.RequestBodyUriSpec spec = webClient.post();
        WebClient.RequestHeadersSpec<?> requestSpec = spec.uri(uriBuilder -> uriBuilder
                        .scheme(extractScheme(serviceBaseUrl))
                        .host(extractHost(serviceBaseUrl))
                        .port(extractPort(serviceBaseUrl))
                        .path(path)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                // 使用 BodyInserters.fromValue 会自动使用 Jackson 序列化为 JSON
                .body(BodyInserters.fromValue(requestBody));

        if (!CollectionUtils.isEmpty(headers)) {
            requestSpec = requestSpec.headers(httpHeaders -> httpHeaders.setAll(headers));
        }

        return requestSpec
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> handleErrorResponse("POST", serviceBaseUrl, path, response))
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(20)) // AI 推理可能稍久一点
                .doOnError(e -> log.error("POST 调用 Python 服务异常, url={}", url, e));
    }

    /**
     * 概述:
     *  以 POST JSON 方式调用 Python 服务, 期望返回 "流式文本"(如 LLM 流式结果).
     * 功能:
     *  1. 适用于 Python 端采用 text/event-stream 或 text/plain 分段推送的场景.
     *  2. 以 Flux<String> 的形式逐段消费响应.
     */
    public Flux<String> postJsonStream(String serviceBaseUrl,
                                       String path,
                                       Object requestBody,
                                       Map<String, String> headers) {
        String url = serviceBaseUrl + path;

        WebClient.RequestBodyUriSpec spec = webClient.post();
        WebClient.RequestHeadersSpec<?> requestSpec = spec.uri(uriBuilder -> uriBuilder
                        .scheme(extractScheme(serviceBaseUrl))
                        .host(extractHost(serviceBaseUrl))
                        .port(extractPort(serviceBaseUrl))
                        .path(path)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody));

        if (!CollectionUtils.isEmpty(headers)) {
            requestSpec = requestSpec.headers(httpHeaders -> httpHeaders.setAll(headers));
        }

        return requestSpec
                // AI 流式返回常见为 text/event-stream 或 text/plain
                .accept(MediaType.TEXT_EVENT_STREAM, MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> handleErrorResponse("POST_STREAM", serviceBaseUrl, path, response))
                // 将底层 DataBuffer 转换为 String 流
                .bodyToFlux(String.class)
                .doOnSubscribe(s -> log.info("开始流式调用 Python 服务, url={}", url))
                .doOnError(e -> log.error("流式调用 Python 服务异常, url={}", url, e));
    }

    /**
     * 概述:
     *  统一处理 HTTP 非 2xx 状态码, 将其转换为 PythonServiceException.
     * 入参:
     *  method: HTTP 方法, 如 GET/POST.
     *  serviceBaseUrl: 服务基础地址.
     *  path: 业务路径.
     *  response: ClientResponse 对象, 用于读取状态码和响应体.
     * 出参:
     *  Mono<PythonServiceException>, 交由上层 retrieve 执行链抛出.
     */
    private Mono<? extends Throwable> handleErrorResponse(String method,
                                                          String serviceBaseUrl,
                                                          String path,
                                                          ClientResponse response) {
        String url = serviceBaseUrl + path;
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(body -> {
                    int status = response.statusCode().value();
                    String truncatedBody = body;
                    // 只保留前 200 字符, 避免日志过长
                    if (body.length() > 200) {
                        truncatedBody = body.substring(0, 200) + "...";
                    }
                    String msg = String.format("调用 Python 服务失败, method=%s, url=%s, status=%d, body=%s",
                            method, url, status, truncatedBody);
                    return new PythonServiceException(msg,
                            serviceBaseUrl,
                            path,
                            status,
                            truncatedBody,
                            null);
                });
    }

    // 以下为简单的 URL 解析工具方法, 实际项目中可以换成更稳健的 URI 构造逻辑

    private String extractScheme(String baseUrl) {
        // 简单实现: http://host:port 或 https://host:port
        if (baseUrl.startsWith("https://")) {
            return "https";
        }
        return "http";
    }

    private String extractHost(String baseUrl) {
        String tmp = baseUrl.replace("http://", "").replace("https://", "");
        int colonIndex = tmp.indexOf(':');
        if (colonIndex > 0) {
            return tmp.substring(0, colonIndex);
        }
        int slashIndex = tmp.indexOf('/');
        if (slashIndex > 0) {
            return tmp.substring(0, slashIndex);
        }
        return tmp;
    }

    private int extractPort(String baseUrl) {
        String tmp = baseUrl.replace("http://", "").replace("https://", "");
        int colonIndex = tmp.indexOf(':');
        if (colonIndex > 0) {
            String portStr = tmp.substring(colonIndex + 1);
            int slashIndex = portStr.indexOf('/');
            if (slashIndex > 0) {
                portStr = portStr.substring(0, slashIndex);
            }
            try {
                return Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                // 默认端口
                return baseUrl.startsWith("https") ? 443 : 80;
            }
        }
        // 未指定端口时返回默认端口
        return baseUrl.startsWith("https") ? 443 : 80;
    }
}
```

#### 七、使用示例

1 普通 JSON 请求示例（同步使用）

```java
// 假设 Python 端有接口: POST http://python-ai:8000/api/v1/embedding
// 请求体: EmbeddingRequest, 响应体: EmbeddingResponse

@Autowired
private PythonServiceClient pythonServiceClient;

public EmbeddingResponse callEmbedding(EmbeddingRequest request, String traceId) {
    String baseUrl = "http://python-ai:8000";
    String path = "/api/v1/embedding";

    Map<String, String> headers = new java.util.HashMap<>();
    headers.put("traceId", traceId);

    // 同步等待结果(传统调用方式)
    return pythonServiceClient.postJson(baseUrl, path, request, headers, EmbeddingResponse.class)
            .block();
}
```

2 流式调用 LLM 示例（前端 SSE 推流）

```java
// 假设 Python 端 SSE 接口: POST http://python-llm:8001/api/v1/chat/stream
// 返回 text/event-stream 或分行文本

public Flux<String> streamChat(ChatRequest request, String traceId) {
    String baseUrl = "http://python-llm:8001";
    String path = "/api/v1/chat/stream";

    Map<String, String> headers = new java.util.HashMap<>();
    headers.put("traceId", traceId);

    // 返回 Flux<String>, 控制层可以直接写到响应流中(SSE).
    return pythonServiceClient.postJsonStream(baseUrl, path, request, headers);
}
```

3 响应式组合调用示例（先 RAG 再 LLM）

```java
public Mono<String> ragThenChat(RagRequest ragRequest, ChatRequest chatRequest, String traceId) {
    String ragBaseUrl = "http://python-rag:9000";
    String ragPath = "/api/v1/rag";

    Map<String, String> headers = new java.util.HashMap<>();
    headers.put("traceId", traceId);

    // 先 RAG 获取上下文
    Mono<RagResponse> ragMono = pythonServiceClient.postJson(ragBaseUrl, ragPath, ragRequest, headers, RagResponse.class);

    // 再调用 LLM, 并通过 Reactor 链式拼接
    return ragMono.flatMap(ragResp -> {
        // 将 RAG 结果填入 ChatRequest
        chatRequest.setContext(ragResp.getContext());
        String llmBaseUrl = "http://python-llm:8001";
        String llmPath = "/api/v1/chat";

        return pythonServiceClient.postJson(llmBaseUrl, llmPath, chatRequest, headers, ChatResponse.class)
                .map(ChatResponse::getAnswer);
    });
}
```

#### 八、注意事项与扩展建议

1 traceId 和链路追踪

* 建议在所有 headers 中透传 traceId、spanId 等信息
* 若使用 MDC, 可在调用前从 MDC 中读取并塞入 headers
  2 超时时间策略
* 连接、读写超时在 WebClientConfig 中统一设置
* 针对单个调用可以使用 Mono.timeout 单独限制
  3 错误处理统一化
* 所有非 2xx 状态码统一转换为 PythonServiceException
* 在上层全局异常处理器中捕获, 做日志与降级
  4 性能与连接池
* ConnectionProvider 的 maxConnections 和 pendingAcquireMaxCount 需结合压测结果调优
* AI 场景通常是 I/O 密集, 注意不要开太多业务线程, 尽量利用 Reactor 模型
  5 与网关或服务发现整合
* 当前示例用 serviceBaseUrl 直接写死地址
* 实际项目中可以改为从 Nacos/Consul/Eureka 或 API Gateway 中获取地址
  6 与前端流式推送
* 控制层可使用 WebFlux 的 SseEmitter 或直接返回 Flux<ServerSentEvent<String>>
* Python 端建议使用 text/event-stream 或分行 JSON, 方便逐段解析
  如果你愿意, 下一步我可以帮你：
  1 直接给出配套的 Controller 层示例, 从浏览器前端 SSE 一路打通到 Python LLM 服务
  2 为这套 PythonServiceClient 画一张简洁的架构图(适配你的 Java → Python → LLM 全链路)
  3 根据你的实际项目结构(是否 Spring Cloud, 是否有网关)做一次定制化重构建议
