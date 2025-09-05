package com.st.modules.thread.framework.v3;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 编排服务：
 * - allOf 聚合：单个失败填充 fallback
 * - anyOf 快速副本：先完成即返回
 * - 同步优先，超时 → 票据（taskId），后台完成可查询
 */


public final class OrchestratorService {

    private final Engine engine;
    private final Dispatcher dispatcher;
    private final TicketStore ticketStore;

    public OrchestratorService(Engine engine, Dispatcher dispatcher, TicketStore ticketStore) {
        this.engine = engine; this.dispatcher = dispatcher; this.ticketStore = ticketStore;
    }

    /** 并行聚合（按名称构建任务，仅示例） */
    public List<String> aggregate(List<String> names, long globalTimeoutMs) {
        Deadline ddl = Deadline.after(globalTimeoutMs, TimeUnit.MILLISECONDS);
        List<Supplier<String>> tasks = new ArrayList<Supplier<String>>(names.size());
        for (String n : names) {
            long step = Engine.stepTimeoutMs(ddl, 200); // 每步兜底 200ms
            tasks.add(() -> simulateIO("A:" + n, step));
        }
        try {
            return engine.allWithFallback(tasks, ex -> "fallback", globalTimeoutMs, TimeUnit.MILLISECONDS).get();
        } catch (Exception e) {
            return Collections.<String>emptyList();
        }
    }

    /** 快速副本（anyOf） */
    public String fastReplica(String key, long timeoutMs) {
        List<Supplier<String>> replicas = Arrays.<Supplier<String>>asList(
                () -> simulateIO("az1:" + key, 120),
                () -> simulateIO("az2:" + key, 150)
        );
        try { return engine.anyOfOrFallback(replicas, "fallback", timeoutMs, TimeUnit.MILLISECONDS).get(); }
        catch (Exception e) { return "fallback"; }
    }

    /** 同步优先，超时转票据 */
    public Map<String, Object> submitWithTicket(String name, long waitMs) {
        ObjectNode meta = JsonNodeFactory.instance.objectNode();
        AsyncTaskContext ctx = new AsyncTaskContext(meta);
        String taskId = ctx.taskId();

        CompletableFuture<String> promise = new CompletableFuture<String>();
        dispatcher.dispatchWithRetry(ctx, () -> simulateIO("job:" + name, 150),
                new Callback<String>() {
                    public void onSuccess(AsyncTaskContext c, String result) {
                        c.success(result); ticketStore.save(taskId, result); promise.complete(result);
                    }
                    public void onFailure(AsyncTaskContext c, Throwable ex) {
                        c.fail(ex); ticketStore.save(taskId, "fallback"); promise.complete("fallback");
                    }
                },
                2, Backoff.expoJitter(50, 200, TimeUnit.MILLISECONDS)
        );

        try {
            String v = promise.get(waitMs, TimeUnit.MILLISECONDS);
            Map<String,Object> ok = new HashMap<String,Object>();
            ok.put("taskId", taskId); ok.put("status","SUCCESS"); ok.put("data", v);
            return ok;
        } catch (TimeoutException te) {
            Map<String,Object> ticket = new HashMap<String,Object>();
            ticket.put("taskId", taskId); ticket.put("status","PROCESSING");
            return ticket;
        } catch (Exception e) {
            Map<String,Object> err = new HashMap<String,Object>();
            err.put("taskId", taskId); err.put("status","FAILED");
            return err;
        }
    }

    public Object queryTicket(String taskId) {
        Object v = ticketStore.get(taskId);
        return v == null ? "PROCESSING" : v;
    }

    /* ----------- 示例 I/O ------------ */
    private static String simulateIO(String tag, long ms) {
        try { Thread.sleep(Math.max(1, ms)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        if ((tag.hashCode() & 7) == 0) throw new RuntimeException("boom"); // 偶发错误，演示重试/回退
        return tag;
    }
}

