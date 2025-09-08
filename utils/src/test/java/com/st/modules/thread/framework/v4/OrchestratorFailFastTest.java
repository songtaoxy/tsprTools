package com.st.modules.thread.framework.v4;

// src/test/java/com/best/concurrent/tests/OrchestratorFailFastTest.java

import com.st.modules.thread.framework.v4.core.Engine;
import com.st.modules.thread.framework.v4.core.TaskWrappers;
import com.st.modules.thread.framework.v4.orchestration.InMemoryTicketStore;
import com.st.modules.thread.framework.v4.orchestration.OrchestratorService;
import com.st.modules.thread.framework.v4.orchestration.TicketStore;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 注意：OrchestratorService 默认 doBusiness 会对 name 哈希做一次随机失败：
 * (name.hashCode() & 7) == 0 → 抛异常。
 * 这里用 "h" 触发失败；"a/b/c/d" 不会触发随机失败。
 */
public class OrchestratorFailFastTest {

    private Engine engine;
    private TicketStore store;
    private OrchestratorService orchestrator;

    @BeforeEach
    void init() {
        engine = Engine.newBuilder().poolName("hard").core(4).max(8).queue(256).keepAliveSeconds(30)
                .taskWrapper(TaskWrappers.bizAndMdcCapture()).build();
        store = new InMemoryTicketStore();
        orchestrator = new OrchestratorService(engine, store);
    }
    @AfterEach void shutdown(){ engine.shutdown(); }

    @Test
    void allOk_thenSuccess() {
        Map<String,Object> res = orchestrator.allFailFastAggregate(Arrays.asList("a","b","c"), 800, 300);
        assertEquals("SYNC", res.get("mode"));
        assertEquals("SUCCESS", res.get("status"));
    }

    @Test
    void anyFail_thenFailedImmediately() {
        Map<String,Object> res = orchestrator.allFailFastAggregate(Arrays.asList("a","h","c"), 800, 300);
        assertEquals("SYNC", res.get("mode"));
        assertEquals("FAILED", res.get("status"));
    }
}
