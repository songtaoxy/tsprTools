package com.st.modules.thread.framework.v2;



import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * 概述：基于配置构建 CfEngine 并在关闭时优雅下线
 * 功能清单：属性绑定、Bean 暴露、关闭钩子
 * 使用示例：配置见 application.yml，注入 CfEngine 使用
 * 注意事项：确保 core<=max，queue 合理
 * 入参与出参与异常说明：无
 */
@Configuration
@ConfigurationProperties(prefix = "st.concurrent.biz")
public class CfAutoConfig {

    private String poolName = "biz-pool";
    private int core = 8;
    private int max = 32;
    private int queue = 256;
    private int bulkheadPermits = 0;
    private boolean rateLimitEnabled = false;
    private long rateLimitQps = 0;
    private long rateBurst = 0;
    private boolean breakerEnabled = false;
    private int breakerFailureThreshold = 5;
    private long breakerOpenMillis = 2000;
    private int breakerHalfOpenPermits = 2;
    private long shutdownWaitSeconds = 20;

    private CfEngine engine;

    @Bean
    public CfEngine cfEngine() {
        CfEngine.Builder b = CfEngine.newBuilder()
                .poolName(poolName)
                .core(core)
                .max(max)
                .queue(queue)
                .bulkhead(bulkheadPermits);
        if (rateLimitEnabled) b.rateLimiter(new TokenBucketLimiter(rateLimitQps, rateBurst));
        if (breakerEnabled) b.circuitBreaker(new CircuitBreaker(breakerFailureThreshold, breakerOpenMillis, breakerHalfOpenPermits));
        this.engine = b.build();
        return engine;
    }

    @PreDestroy
    public void onClose() {
        if (engine != null) engine.shutdownGracefully(shutdownWaitSeconds, TimeUnit.SECONDS);
    }

    public String getPoolName() { return poolName; }
    public void setPoolName(String poolName) { this.poolName = poolName; }
    public int getCore() { return core; }
    public void setCore(int core) { this.core = core; }
    public int getMax() { return max; }
    public void setMax(int max) { this.max = max; }
    public int getQueue() { return queue; }
    public void setQueue(int queue) { this.queue = queue; }
    public int getBulkheadPermits() { return bulkheadPermits; }
    public void setBulkheadPermits(int bulkheadPermits) { this.bulkheadPermits = bulkheadPermits; }
    public boolean isRateLimitEnabled() { return rateLimitEnabled; }
    public void setRateLimitEnabled(boolean rateLimitEnabled) { this.rateLimitEnabled = rateLimitEnabled; }
    public long getRateLimitQps() { return rateLimitQps; }
    public void setRateLimitQps(long rateLimitQps) { this.rateLimitQps = rateLimitQps; }
    public long getRateBurst() { return rateBurst; }
    public void setRateBurst(long rateBurst) { this.rateBurst = rateBurst; }
    public boolean isBreakerEnabled() { return breakerEnabled; }
    public void setBreakerEnabled(boolean breakerEnabled) { this.breakerEnabled = breakerEnabled; }
    public int getBreakerFailureThreshold() { return breakerFailureThreshold; }
    public void setBreakerFailureThreshold(int breakerFailureThreshold) { this.breakerFailureThreshold = breakerFailureThreshold; }
    public long getBreakerOpenMillis() { return breakerOpenMillis; }
    public void setBreakerOpenMillis(long breakerOpenMillis) { this.breakerOpenMillis = breakerOpenMillis; }
    public int getBreakerHalfOpenPermits() { return breakerHalfOpenPermits; }
    public void setBreakerHalfOpenPermits(int breakerHalfOpenPermits) { this.breakerHalfOpenPermits = breakerHalfOpenPermits; }
    public long getShutdownWaitSeconds() { return shutdownWaitSeconds; }
    public void setShutdownWaitSeconds(long shutdownWaitSeconds) { this.shutdownWaitSeconds = shutdownWaitSeconds; }
}

