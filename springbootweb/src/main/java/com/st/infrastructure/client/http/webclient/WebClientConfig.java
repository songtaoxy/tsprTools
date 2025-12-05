package com.st.infrastructure.client.http.webclient;

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

