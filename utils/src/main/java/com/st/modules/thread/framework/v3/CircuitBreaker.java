package com.st.modules.thread.framework.v3;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/** 简化熔断器（CLOSED → OPEN → HALF_OPEN） */
public final class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }
    private final int failureThreshold, halfOpenPermits;
    private final long openMillis;
    private final AtomicInteger failures = new AtomicInteger(0);
    private final AtomicInteger halfOpenInUse = new AtomicInteger(0);
    private final AtomicLong openUntil = new AtomicLong(0);
    private volatile State state = State.CLOSED;

    public CircuitBreaker(int failureThreshold, long openMillis, int halfOpenPermits) {
        if (failureThreshold <= 0 || openMillis <= 0 || halfOpenPermits <= 0) throw new IllegalArgumentException();
        this.failureThreshold = failureThreshold; this.openMillis = openMillis; this.halfOpenPermits = halfOpenPermits;
    }

    public boolean tryPass() {
        long now = System.currentTimeMillis();
        State s = state;
        if (s == State.CLOSED) return true;
        if (s == State.OPEN) {
            if (now >= openUntil.get()) { state = State.HALF_OPEN; halfOpenInUse.set(0); }
            else return false;
        }
        if (state == State.HALF_OPEN) return halfOpenInUse.incrementAndGet() <= halfOpenPermits;
        return true;
    }

    public void onSuccess() {
        if (state == State.HALF_OPEN) {
            if (halfOpenInUse.decrementAndGet() <= 0) reset();
        } else failures.set(0);
    }

    public void onFailure() {
        if (state == State.HALF_OPEN) { halfOpenInUse.decrementAndGet(); tripOpen(); return; }
        if (failures.incrementAndGet() >= failureThreshold) tripOpen();
    }

    private void tripOpen() { state = State.OPEN; openUntil.set(System.currentTimeMillis() + openMillis); }
    private void reset() { failures.set(0); state = State.CLOSED; }
    public State state() { return state; }
}

