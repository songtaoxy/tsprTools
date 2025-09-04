package com.st.modules.thread.framework.v3;

/** 简洁线程安全令牌桶（QPS + 突发容量） */
public final class TokenBucketLimiter {
    private final long capacity, tokensPerSecond;
    private double tokens;
    private long lastNanos;

    public TokenBucketLimiter(long tokensPerSecond, long capacity) {
        if (tokensPerSecond <= 0 || capacity <= 0) throw new IllegalArgumentException();
        this.tokensPerSecond = tokensPerSecond; this.capacity = capacity;
        this.tokens = capacity; this.lastNanos = System.nanoTime();
    }

    public synchronized boolean tryAcquire(int permits) {
        refill();
        if (tokens >= permits) { tokens -= permits; return true; }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double deltaSec = (now - lastNanos) / 1_000_000_000.0;
        double add = deltaSec * tokensPerSecond;
        if (add > 0) { tokens = Math.min(capacity, tokens + add); lastNanos = now; }
    }
}
