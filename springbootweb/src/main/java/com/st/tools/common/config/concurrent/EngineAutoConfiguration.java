package com.st.tools.common.config.concurrent;


import com.st.modules.thread.framework.v3.*;
import com.st.modules.thread.framework.v3.OrchestratorService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/** 自动装配：基于配置创建 Engine、Dispatcher、Orchestrator 与 TicketStore */
@Configuration
@ConfigurationProperties(prefix = "best.concurrent")
public class EngineAutoConfiguration {

    // ---- 属性（可在 application.yml 配置） ----
    private String poolName = "biz";
    private int core = 8, max = 32, queue = 256;
    private long keepAliveSeconds = 60;
    private int bulkheadPermits = 0;

    private boolean rateLimitEnabled = true;
    private long rateLimitQps = 300, rateBurst = 600;

    private boolean breakerEnabled = true;
    private int breakerFailureThreshold = 5, breakerHalfOpenPermits = 2;
    private long breakerOpenMillis = 2000;

    private long shutdownWaitSeconds = 20;

    private Engine engine;

    @Bean
    public Engine engine() {
        Engine.Builder b = Engine.newBuilder()
                .poolName(poolName).core(core).max(max).queue(queue).keepAliveSeconds(keepAliveSeconds)
                .bulkhead(bulkheadPermits);
        if (rateLimitEnabled) b.rateLimiter(new TokenBucketLimiter(rateLimitQps, rateBurst));
        if (breakerEnabled) b.circuitBreaker(new CircuitBreaker(breakerFailureThreshold, breakerOpenMillis, breakerHalfOpenPermits));
        this.engine = b.build();
        return engine;
    }

    @Bean public Dispatcher dispatcher(Engine engine) { return new Dispatcher(engine); }
    @Bean public TicketStore ticketStore() { return new InMemoryTicketStore(); }
    @Bean public OrchestratorService orchestratorService(Engine engine, Dispatcher dispatcher, TicketStore store) {
        return new OrchestratorService(engine, dispatcher, store);
    }

    @PreDestroy public void onClose() {
        if (engine != null) engine.shutdownGracefully(shutdownWaitSeconds, TimeUnit.SECONDS);
    }

    // ---- setters for @ConfigurationProperties ----
    public void setPoolName(String poolName) { this.poolName = poolName; }
    public void setCore(int core) { this.core = core; }
    public void setMax(int max) { this.max = max; }
    public void setQueue(int queue) { this.queue = queue; }
    public void setKeepAliveSeconds(long keepAliveSeconds) { this.keepAliveSeconds = keepAliveSeconds; }
    public void setBulkheadPermits(int bulkheadPermits) { this.bulkheadPermits = bulkheadPermits; }
    public void setRateLimitEnabled(boolean rateLimitEnabled) { this.rateLimitEnabled = rateLimitEnabled; }
    public void setRateLimitQps(long rateLimitQps) { this.rateLimitQps = rateLimitQps; }
    public void setRateBurst(long rateBurst) { this.rateBurst = rateBurst; }
    public void setBreakerEnabled(boolean breakerEnabled) { this.breakerEnabled = breakerEnabled; }
    public void setBreakerFailureThreshold(int breakerFailureThreshold) { this.breakerFailureThreshold = breakerFailureThreshold; }
    public void setBreakerHalfOpenPermits(int breakerHalfOpenPermits) { this.breakerHalfOpenPermits = breakerHalfOpenPermits; }
    public void setBreakerOpenMillis(long breakerOpenMillis) { this.breakerOpenMillis = breakerOpenMillis; }
    public void setShutdownWaitSeconds(long shutdownWaitSeconds) { this.shutdownWaitSeconds = shutdownWaitSeconds; }
}

