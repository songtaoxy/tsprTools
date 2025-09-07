package com.st.modules.thread.framework.v4.test;

// src/test/java/com/best/concurrent/tests/TestHelpers.java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.fail;

public final class TestHelpers {
    private TestHelpers(){}

    public static void sleep(long ms){
        try { Thread.sleep(ms); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
    }

    /** 轮询等待条件达成（最多 waitMs），否则 fail */
    public static void awaitTrue(BooleanSupplier cond, long waitMs, String message){
        long deadline = System.currentTimeMillis() + waitMs;
        while (System.currentTimeMillis() < deadline) {
            if (cond.getAsBoolean()) return;
            sleep(10);
        }
        fail(message);
    }

    /** 倒计时工具 */
    public static void awaitLatch(CountDownLatch cdl, long waitMs, String message) {
        try {
            if (!cdl.await(waitMs, TimeUnit.MILLISECONDS)) fail(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("interrupted while waiting for latch");
        }
    }
}

