package com.st.modules.thread.framework.v4;

import static org.junit.jupiter.api.Assertions.*;

// src/test/java/com/best/concurrent/tests/EngineTest.java
import com.st.modules.thread.framework.v4.test.TestHelpers;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {

    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = Engine.newBuilder()
                .poolName("test")
                .core(4).max(8).queue(256).keepAliveSeconds(30)
                .taskWrapper(TaskWrappers.bizAndMdcCapture())
                .build();
    }

    @AfterEach
    void tearDown() { engine.shutdown(); }

    @Test
    void supply_success() {
        String v = engine.supply(() -> "ok", 100, TimeUnit.MILLISECONDS, () -> "fb", false)
                .join();
        assertEquals("ok", v);
    }

    @Test
    void supply_exception_fallback() {
        String v = engine.supply(() -> { throw new RuntimeException("boom"); }, 200, TimeUnit.MILLISECONDS,
                () -> "fb", false).join();
        assertEquals("fb", v);
    }

    @Test
    void supply_timeout_no_cancel() {
        AtomicBoolean interrupted = new AtomicBoolean(false);
        String v = engine.supply(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e){ interrupted.set(true); Thread.currentThread().interrupt(); }
            return "late";
        }, 50, TimeUnit.MILLISECONDS, () -> "fb", /*cancelOnTimeout=*/false).join();

        assertEquals("fb", v);
        assertFalse(interrupted.get(), "取消策略为不打断，任务不应被中断");
    }

    @Test
    void supply_timeout_cancel() {
        AtomicBoolean interrupted = new AtomicBoolean(false);
        String v = engine.supply(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e){ interrupted.set(true); Thread.currentThread().interrupt(); }
            return "late";
        }, 50, TimeUnit.MILLISECONDS, () -> "fb", /*cancelOnTimeout=*/true).join();

        assertEquals("fb", v);
        assertTrue(interrupted.get(), "应被中断以快速释放资源");
    }

    @Test
    void supplyFailFast_exception() {
        CompletableFuture<String> cf = engine.supplyFailFast(() -> { throw new RuntimeException("boom"); },
                0, TimeUnit.MILLISECONDS, false);
        ExecutionException ex = assertThrows(ExecutionException.class, cf::get);
        assertEquals("boom", ex.getCause().getMessage());
    }

    @Test
    void supplyFailFast_timeout_exception() {
        CompletableFuture<String> cf = engine.supplyFailFast(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
            return "late";
        }, 50, TimeUnit.MILLISECONDS, true);
        assertThrows(ExecutionException.class, cf::get);
    }

    @Test
    void allWithFallback_mixed() {
        List<Supplier<String>> tasks = Arrays.asList(
                () -> "A",
                () -> { throw new RuntimeException("boom"); },
                () -> "C"
        );
        List<String> out = engine.allWithFallback(tasks, t -> "FB", 200, TimeUnit.MILLISECONDS).join();
        assertEquals(Arrays.asList("A", "FB", "C"), out);
    }

    @Test
    void allFailFast_anyFailureFailsAll() {
        List<Supplier<String>> tasks = Arrays.asList(
                () -> { sleep(80); return "A"; },
                () -> { throw new RuntimeException("boom"); },
                () -> { sleep(120); return "C"; }
        );
        CompletableFuture<List<String>> cf = engine.allFailFast(tasks, 500, TimeUnit.MILLISECONDS, true);
        assertThrows(ExecutionException.class, cf::get);
    }

    @Test
    void anyOfOrFallback_firstWins() {
        List<Supplier<String>> replicas = Arrays.asList(
                () -> { sleep(120); return "SLOW"; },
                () -> "FAST",
                () -> { throw new RuntimeException("boom"); }
        );
        String v = engine.anyOfOrFallback(replicas, () -> "FB", 300, TimeUnit.MILLISECONDS).join();
        assertEquals("FAST", v);
    }

    @Test
    void anyOfOrFallback_allFail_thenFallback() {
        List<Supplier<String>> replicas = Arrays.asList(
                () -> { throw new RuntimeException("e1"); },
                () -> { throw new RuntimeException("e2"); }
        );
        String v = engine.anyOfOrFallback(replicas, () -> "FB", 100, TimeUnit.MILLISECONDS).join();
        assertEquals("FB", v);
    }

    @Test
    void withTimeout_doesNotCancelUnderlying() throws Exception {
        CompletableFuture<String> slow = new CompletableFuture<>();
        CompletableFuture<String> out = engine.withTimeout(slow, 50, TimeUnit.MILLISECONDS);
        assertTrue(out.isCompletedExceptionally(), "外层应在 50ms 超时（异步检查需要稍等）");
        // 填充底层
        slow.complete("LATE");
        // 外层已超时不会变回成功，但底层确实完成了（证明未取消）
        assertTrue(out.isCompletedExceptionally());
        assertEquals("LATE", slow.get());
    }

    @Test
    void retrySync_successOnSecondAttempt() throws Exception {
        final int[] times = {0};
        String v = Engine.retrySync(() -> {
            if (++times[0] == 1) throw new RuntimeException("transient");
            return "OK";
        }, (Predicate<Throwable>) th -> true, 3, 50, 200, 0.2);
        assertEquals("OK", v);
        assertEquals(2, times[0]);
    }

    private static void sleep(long ms){ TestHelpers.sleep(ms); }
}
