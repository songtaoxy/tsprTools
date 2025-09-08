package com.st.modules.thread.framework.v4;

// src/test/java/com/best/concurrent/tests/TicketStorePubSubTest.java

import com.st.modules.thread.framework.v4.orchestration.InMemoryTicketStore;
import com.st.modules.thread.framework.v4.orchestration.Ticket;
import com.st.modules.thread.framework.v4.test.TestHelpers;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TicketStorePubSubTest {

    @Test
    void publishAndSubscribe() {
        InMemoryTicketStore store = new InMemoryTicketStore();
        Ticket t = new Ticket("T-1", Arrays.asList("a","b"), false);
        store.saveTicket(t);

        AtomicInteger got = new AtomicInteger(0);
        CountDownLatch cdl = new CountDownLatch(1);

        java.util.function.Consumer<Ticket> listener = new java.util.function.Consumer<Ticket>() {
            public void accept(Ticket ticket) { got.incrementAndGet(); cdl.countDown(); }
        };

        store.subscribe(t.taskId, listener);
        t.progress = 50; store.publishUpdate(t.taskId, t);

        TestHelpers.awaitLatch(cdl, 500, "未收到更新事件");
        assertTrue(got.get() >= 1);
        store.unsubscribe(t.taskId, listener);
    }
}
