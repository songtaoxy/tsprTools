package com.st.modules.thread.framework.v2;


import java.util.concurrent.TimeUnit;

/**
 * 概述：重试退避策略
 * 功能清单：计算第 attempt 次重试的等待时长
 * 使用示例：Backoff fixed = Backoff.fixed(80, MILLISECONDS)
 * 注意事项：attempt 从 1 开始
 * 入参与出参与异常说明：工厂方法入参为时长与单位；无异常
 */
public interface Backoff {
    long nextDelayMillis(int attempt);
    static Backoff fixed(final long delay, final TimeUnit unit) {
        final long ms = unit.toMillis(delay);
        return attempt -> ms;
    }
    static Backoff expo(final long base, final long max, final TimeUnit unit) {
        final long baseMs = unit.toMillis(base);
        final long maxMs = unit.toMillis(max);
        return attempt -> Math.min(maxMs, baseMs << Math.max(0, attempt - 1));
    }
}
