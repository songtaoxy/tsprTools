package com.st.infrastructure.client.http.webclient.old;

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
