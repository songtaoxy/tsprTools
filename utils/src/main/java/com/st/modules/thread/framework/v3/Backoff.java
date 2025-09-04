package com.st.modules.thread.framework.v3;


import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/** 重试退避策略（固定/指数/指数+抖动） */
public interface Backoff {
    long nextDelayMillis(int attempt);

    static Backoff fixed(long delay, TimeUnit unit) {
        final long ms = unit.toMillis(delay);
        return attempt -> ms;
    }
    static Backoff expo(long base, long max, TimeUnit unit) {
        final long b = unit.toMillis(base), m = unit.toMillis(max);
        return attempt -> Math.min(m, b << Math.max(0, attempt - 1));
    }
    static Backoff expoJitter(long base, long max, TimeUnit unit) {
        final long b = unit.toMillis(base), m = unit.toMillis(max);
        return attempt -> {
            long cap = Math.min(m, b << Math.max(0, attempt - 1));
            return ThreadLocalRandom.current().nextLong(Math.max(1, cap / 2), Math.max(2, cap));
        };
    }
}

