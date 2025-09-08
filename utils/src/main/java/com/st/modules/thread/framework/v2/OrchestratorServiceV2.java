package com.st.modules.thread.framework.v2;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 概述：面向控制器的编排服务，提供并行聚合、快速副本、全局超时与票据式异步
 * 功能清单：
 * 1）processAll：对一组任务并行allOf聚合，单个失败用占位，支持全局deadline
 * 2）processAny：主备anyOf，先完成即返回，支持全局timeout与最终降级
 * 3）processWithTicket：同步优先，超时则返回taskId票据，后台完成后可查询
 * 使用示例：见 Controller 或 Main
 * 注意事项：names/inputs 为示例，替换为你的真实任务构造逻辑
 * 入参与出参与异常说明：异常以占位或ticket形式收口；不抛检查异常
 */
public class OrchestratorServiceV2 {

    private final CoreServiceV2 coreService;
    private final DispatcherAdapter dispatcher;
    private final CfEngine engine;
    private final ConcurrentMap<String, Object> ticketStore = new ConcurrentHashMap<String, Object>();

    public OrchestratorServiceV2(CoreServiceV2 coreService, DispatcherAdapter dispatcher, CfEngine engine) {
        this.coreService = coreService;
        this.dispatcher = dispatcher;
        this.engine = engine;
    }

    public List<String> processAll(List<String> names, long globalTimeoutMs) {
        final ObjectNode meta = JsonNodeFactory.instance.objectNode();
        final AsyncTaskContext ctx = AsyncTaskContext.newContext(meta);
        List<Supplier<String>> tasks = new ArrayList<Supplier<String>>();
        for (final String n : names) {
            tasks.add(new Supplier<String>() {
                public String get() {
                    return coreService.handleBusiness("job-" + n, ctx.getMetadata());
                }
            });
        }
        try {
            List<String> out = engine.allWithFallback(tasks, new java.util.function.Function<Throwable, String>() {
                public String apply(Throwable ex) { return "fallback"; }
            }, globalTimeoutMs, TimeUnit.MILLISECONDS).get();
            ctx.complete(out);
            return out;
        } catch (Exception e) {
            ctx.fail(e);
            return java.util.Collections.<String>emptyList();
        }
    }

    public String processAny(final String key, long timeoutMs) {
        final ObjectNode meta = JsonNodeFactory.instance.objectNode();
        final AsyncTaskContext ctx = AsyncTaskContext.newContext(meta);
        List<Supplier<String>> replicas = Arrays.<Supplier<String>>asList(
                new Supplier<String>() { public String get() { return coreService.handleBusiness("az1:" + key, meta); } },
                new Supplier<String>() { public String get() { return coreService.handleBusiness("az2:" + key, meta); } }
        );
        try {
            String out = engine.anyOfOrFallback(replicas, "fallback", timeoutMs, TimeUnit.MILLISECONDS).get();
            ctx.complete(out);
            return out;
        } catch (Exception e) {
            ctx.fail(e);
            return "fallback";
        }
    }

    public Map<String, Object> processWithTicket(final String name, long waitMs) {
        final ObjectNode meta = JsonNodeFactory.instance.objectNode();
        final AsyncTaskContext ctx = AsyncTaskContext.newContext(meta);
        final String taskId = ctx.getTaskId();

        final CompletableFuture<String> cf = new CompletableFuture<String>();
        dispatcher.dispatchWithRetry(ctx,
                new Callable<String>() { public String call() { return coreService.handleBusiness("job-" + name, meta); } },
                new Callback<String>() {
                    public void onSuccess(AsyncTaskContext c, String result) { c.complete(result); ticketStore.put(taskId, result); cf.complete(result); }
                    public void onFailure(AsyncTaskContext c, Throwable ex) { c.fail(ex); ticketStore.put(taskId, "fallback"); cf.complete("fallback"); }
                },
                2,
                Backoff.expo(50, 200, TimeUnit.MILLISECONDS)
        );

        try {
            String v = cf.get(waitMs, TimeUnit.MILLISECONDS);
            Map<String, Object> ok = new HashMap<String, Object>();
            ok.put("taskId", taskId);
            ok.put("status", "SUCCESS");
            ok.put("data", v);
            return ok;
        } catch (TimeoutException te) {
            Map<String, Object> ticket = new HashMap<String, Object>();
            ticket.put("taskId", taskId);
            ticket.put("status", "PROCESSING");
            return ticket;
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String, Object>();
            err.put("taskId", taskId);
            err.put("status", "FAILED");
            return err;
        }
    }

    public Object queryTicket(String taskId) {
        Object v = ticketStore.get(taskId);
        return v == null ? "PROCESSING" : v;
    }
}
