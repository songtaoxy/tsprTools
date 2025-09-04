package com.st.modules.thread.framework.v2;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 概述：简化熔断器（CLOSED OPEN HALF_OPEN）
 * 功能清单：
 * 1）失败计数达到阈值后 OPEN 一段时间
 * 2）OPEN 超时后进入 HALF_OPEN，允许少量探测请求
 * 3）探测成功则 CLOSE，失败则回到 OPEN
 * 使用示例：CircuitBreaker cb = new CircuitBreaker(5, 2000, 2)
 * 注意事项：窗口为计次阈值模型，适合简单场景
 * 入参与出参与异常说明：构造参数为失败阈值、打开时长毫秒、半开允许并发数
 */
public class CircuitBreaker {
    public enum State {CLOSED, OPEN, HALF_OPEN}
    private final int failureThreshold;
    private final long openMillis;
    private final int halfOpenPermits;
    private final AtomicInteger failures = new AtomicInteger(0);
    private final AtomicLong openUntil = new AtomicLong(0);
    private final AtomicInteger halfOpenInUse = new AtomicInteger(0);
    private volatile State state = State.CLOSED;

    public CircuitBreaker(int failureThreshold, long openMillis, int halfOpenPermits) {
        if (failureThreshold <= 0 || openMillis <= 0 || halfOpenPermits <= 0) throw new IllegalArgumentException();
        this.failureThreshold = failureThreshold;
        this.openMillis = openMillis;
        this.halfOpenPermits = halfOpenPermits;
    }

    public boolean tryPass() {
        long now = System.currentTimeMillis();
        State s = state;
        if (s == State.CLOSED) return true;
        if (s == State.OPEN) {
            if (now >= openUntil.get()) {
                state = State.HALF_OPEN;
                halfOpenInUse.set(0);
            } else {
                return false;
            }
        }
        if (state == State.HALF_OPEN) {
            return halfOpenInUse.incrementAndGet() <= halfOpenPermits;
        }
        return true;
    }

    public void onSuccess() {
        if (state == State.HALF_OPEN) {
            if (halfOpenInUse.decrementAndGet() <= 0) {
                reset();
            }
        } else if (state == State.CLOSED) {
            failures.set(0);
        }
    }

    public void onFailure() {
        if (state == State.HALF_OPEN) {
            halfOpenInUse.decrementAndGet();
            tripOpen();
            return;
        }
        int f = failures.incrementAndGet();
        if (f >= failureThreshold) {
            tripOpen();
        }
    }

    private void tripOpen() {
        state = State.OPEN;
        openUntil.set(System.currentTimeMillis() + openMillis);
    }

    private void reset() {
        failures.set(0);
        state = State.CLOSED;
    }

    public State getState() { return state; }
}

