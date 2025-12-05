package com.st.infrastructure.client.http.webclient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.infrastructure.client.http.webclient.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
/**
 * 概述:
 *  面向 AI 场景的 HTTP 客户端工具类, 基于 WebClient + 协议层规范.
 * 功能清单:
 *  1. postJson: 按统一协议调用非流式 JSON 接口, 返回 ApiResponse<T>.
 *  2. postJsonStream: 调用流式接口, 返回事件流(逐行 JSON).
 * 使用示例:
 *  Mono<ApiResponse<EmbeddingResponse>> m = client.postJson(
 *      "http://python-ai:8000", "/api/v1/embedding",
 *      req, headers, new TypeReference<ApiResponse<EmbeddingResponse>>() {});
 *  ApiResponse<EmbeddingResponse> resp = m.block();
 * 注意事项:
 *  1. 方法为异步返回, 若在传统 MVC 环境需 .block() 获取结果.
 *  2. TypeReference 需使用匿名子类传入, 以保留泛型信息.
 * 入参:
 *  baseUrl: Python 服务基础地址.
 *  path: 接口路径.
 *  requestBody: 请求体对象, 将被序列化为 JSON.
 *  headers: 额外 header, 如 traceId.
 *  typeRef: 返回类型, 包含 ApiResponse<T> 的泛型信息.
 * 出参:
 *  postJson: Mono<ApiResponse<T>>.
 *  postJsonStream: Flux<String>, 每个元素是一行 JSON 字符串.
 * 异常说明:
 *  1. HTTP 非 2xx 或 code != OK 时, 抛出 AiServiceException.
 *  2. 网络异常、超时等也包装为 AiServiceException.
 */

@Component
public class AiHttpClient {
    private static final Logger log = LoggerFactory.getLogger(AiHttpClient.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    public AiHttpClient(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }
    public <T> Mono<ApiResponse<T>> postJson(String baseUrl,
                                             String path,
                                             Object requestBody,
                                             java.util.Map<String, String> headers,
                                             TypeReference<ApiResponse<T>> typeRef) {
        String url = baseUrl + path;
        WebClient.RequestBodySpec spec = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON);
        if (headers != null && !headers.isEmpty()) {
            spec = spec.headers(h -> h.setAll(headers));
        }
        // 将请求体对象写入请求
        return spec.body(BodyInserters.fromValue(requestBody))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(20))
                .map(body -> {
                    try {
                        // 将 JSON 字符串解析为 ApiResponse<T>
                        return objectMapper.readValue(body, typeRef);
                    } catch (Exception e) {
                        throw new AiServiceException("解析响应失败: " + body, "INTERNAL_ERROR", headers != null ? headers.getOrDefault("X-Trace-Id", "") : "", e);
                    }
                })
                .flatMap(resp -> {
                    // 检查业务 code
                    if (!resp.isOk()) {
                        throw new AiServiceException("下游返回错误: " + resp.getMsg(), resp.getCode(), resp.getTraceId(), null);
                    }
                    return Mono.just(resp);
                })
                .doOnError(e -> log.error("调用 Python 服务异常, url={}", url, e));
    }
    public Flux<String> postJsonStream(String baseUrl,
                                       String path,
                                       Object requestBody,
                                       java.util.Map<String, String> headers) {
        String url = baseUrl + path;
        WebClient.RequestBodySpec spec = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON);
        if (headers != null && !headers.isEmpty()) {
            spec = spec.headers(h -> h.setAll(headers));
        }
        // 流式场景下, 直接 bodyToFlux(String) 每行一个片段
        return spec.body(BodyInserters.fromValue(requestBody))
                .accept(MediaType.TEXT_EVENT_STREAM, MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(s -> log.info("开始流式调用, url={}", url))
                .doOnError(e -> log.error("流式调用下游异常, url={}", url, e));
    }
}

