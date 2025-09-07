package com.st.modules.thread.framework.v4;

// src/test/java/com/best/concurrent/tests/ContextPropagationTest.java
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ContextPropagationTest {

    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = Engine.newBuilder().poolName("ctx").core(2).max(4).queue(64).keepAliveSeconds(10)
                .taskWrapper(TaskWrappers.bizAndMdcCapture())
                .build();
    }
    @AfterEach void tearDown(){ engine.shutdown(); }

    @Test
    void bizContext_isVisibleInChildThreads() {
        String taskId = UUID.randomUUID().toString();
        BizContext ctx = new BizContext(taskId, "trace-x", "tenant-1", "user-1").put("k","v");
        try (BizContextHolder.Scope __ = BizContextHolder.with(ctx)) {
            String v = engine.supply(() -> {
                BizContext sub = BizContextHolder.get();
                assertNotNull(sub);
                assertEquals(taskId, sub.taskId);
                assertEquals("v", sub.get("k"));
                // 子线程更新属性
                sub.put("stage", "done");
                return "ok";
            }, 200, java.util.concurrent.TimeUnit.MILLISECONDS, () -> "fb", false).join();

            assertEquals("ok", v);
            // 主线程可见子线程更新（共享同一 BizContext 实例）
            assertEquals("done", BizContextHolder.get().get("stage"));
        }
    }
}
