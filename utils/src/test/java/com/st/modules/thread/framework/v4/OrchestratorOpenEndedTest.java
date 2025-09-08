package com.st.modules.thread.framework.v4;

// src/test/java/com/best/concurrent/tests/OrchestratorOpenEndedTest.java

import com.st.modules.thread.framework.v4.core.Engine;
import com.st.modules.thread.framework.v4.core.TaskWrappers;
import com.st.modules.thread.framework.v4.orchestration.InMemoryTicketStore;
import com.st.modules.thread.framework.v4.orchestration.OrchestratorService;
import com.st.modules.thread.framework.v4.orchestration.Ticket;
import com.st.modules.thread.framework.v4.orchestration.TicketStore;
import com.st.modules.thread.framework.v4.test.TestHelpers;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OrchestratorOpenEndedTest {

    private Engine engine;
    private TicketStore store;
    private OrchestratorService orchestrator;

    @BeforeEach
    void init() {
        engine = Engine.newBuilder().poolName("open").core(4).max(8).queue(256).keepAliveSeconds(30)
                .taskWrapper(TaskWrappers.bizAndMdcCapture()).build();
        store = new InMemoryTicketStore();
        orchestrator = new OrchestratorService(engine, store);
    }
    @AfterEach void shutdown(){ engine.shutdown(); }

    @Test
    void syncReturn_whenWithinWaitWindow() {
        // 选择不会随机失败的名字（a,b,c,d）
        Map<String,Object> res = orchestrator.multiSmartAggregateOpenEnded(Arrays.asList("a","b","c"), 500, null);
        assertEquals("SYNC", res.get("mode"));
        assertEquals("SUCCESS", res.get("status"));
        assertEquals(100, res.get("progress"));
        Map summary = (Map) res.get("summary");
        assertEquals(3, summary.get("total"));
        assertEquals(3, summary.get("ok"));
        assertEquals(0, summary.get("fallback"));
    }

    @Test
    void asyncTicket_thenEventuallySuccess() {
        Map<String,Object> res = orchestrator.multiSmartAggregateOpenEnded(Arrays.asList("a","b","c","d"), 50, null);
        assertEquals("ASYNC", res.get("mode"));
        assertEquals("PROCESSING", res.get("status"));
        String taskId = (String) res.get("taskId");
        assertNotNull(taskId);

        // 轮询直到完成
        TestHelpers.awaitTrue(() -> {
            Ticket t = store.getTicket(taskId);
            return t != null && t.status == Ticket.Status.SUCCESS && t.progress == 100;
        }, 2000, "Ticket 未在 2s 内完成");
        Ticket done = store.getTicket(taskId);
        assertEquals(Ticket.Status.SUCCESS, done.status);
        assertEquals(100, done.progress);
        assertEquals(4, done.totalCount);
        assertEquals(0, done.failedCount);
    }

    @Test
    void perTaskTimeout_triggersFallback_butNoCancel() {
        Map<String,Object> res = orchestrator.multiSmartAggregateOpenEnded(Arrays.asList("a","b","c","d"), 500, 10L);
        assertEquals("SYNC", res.get("mode"));
        Map summary = (Map) res.get("summary");
        int fb = (Integer) summary.get("fallback");
        assertTrue(fb >= 1, "步超时应触发至少一个 fallback（具体取决于任务时延）");
    }
}

