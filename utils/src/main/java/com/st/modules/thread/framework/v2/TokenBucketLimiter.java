package com.st.modules.thread.framework.v2;


/**
 * 概述：简单令牌桶限流器
 * 功能清单：
 * 1）按速率补充令牌
 * 2）支持突发容量
 * 使用示例：TokenBucketLimiter limiter = new TokenBucketLimiter(200, 400)
 * 注意事项：线程安全；采用纳秒时间源
 * 入参与出参与异常说明：构造参数为每秒速率与桶容量；tryAcquire 返回是否通过
 */
public class TokenBucketLimiter {
    private final long capacity;
    private final long tokensPerSecond;
    private double tokens;
    private long lastRefillNanos;

    public TokenBucketLimiter(long tokensPerSecond, long capacity) {
        if (tokensPerSecond <= 0 || capacity <= 0) throw new IllegalArgumentException();
        this.tokensPerSecond = tokensPerSecond;
        this.capacity = capacity;
        this.tokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    public synchronized boolean tryAcquire(int permits) {
        refill();
        if (tokens >= permits) {
            tokens -= permits;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double deltaSec = (now - lastRefillNanos) / 1_000_000_000.0;
        double add = deltaSec * tokensPerSecond;
        if (add > 0) {
            tokens = Math.min(capacity, tokens + add);
            lastRefillNanos = now;
        }
    }
}
