package com.st.modules.thread.framework.v4;

// src/test/java/com/best/concurrent/tests/OrchestratorSoftDeadlineTest.java

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

public class OrchestratorSoftDeadlineTest {

    private Engine engine;
    private TicketStore store;
    private OrchestratorService orchestrator;

    @BeforeEach
    void init() {
        engine = Engine.newBuilder().poolName("soft").core(4).max(8).queue(256).keepAliveSeconds(30)
                .taskWrapper(TaskWrappers.bizAndMdcCapture()).build();
        store = new InMemoryTicketStore();
        orchestrator = new OrchestratorService(engine, store);
    }
    @AfterEach void shutdown(){ engine.shutdown(); }

    @Test
    void softDeadline_marksRemainAsFallback_andSuccessOverall() {
        // 设小软截止，确保会有子任务被“强制收尾”
        Map<String,Object> res = orchestrator.multiSmartAggregateSoftNoFail(Arrays.asList("a","b","c","d"),
                100, 200, 80);
        // 可能同步或异步返回，这里两种都接受
        String taskId = (String) res.get("taskId");
        assertNotNull(taskId);

        TestHelpers.awaitTrue(() -> {
            Ticket t = store.getTicket(taskId);
            return t != null && t.status == Ticket.Status.SUCCESS && t.progress == 100;
        }, 3000, "软截止聚合未在 3s 内完成");

        Ticket t = store.getTicket(taskId);
        assertEquals(Ticket.Status.SUCCESS, t.status);
        // 至少一个 fallback（被软截止/或步超时）
        assertTrue(t.fallbackCount >= 1, "软截止后应至少出现一个 fallback");
        assertEquals(0, t.failedCount);
    }
}
