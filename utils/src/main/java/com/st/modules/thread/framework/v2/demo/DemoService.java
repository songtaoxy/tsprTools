package com.st.modules.thread.framework.v2.demo;


import com.st.modules.thread.framework.v2.Backoff;
import com.st.modules.thread.framework.v2.CfEngine;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 概述：示例服务，演示并行聚合、快速副本、重试与降级
 * 功能清单：
 * 1）allWithFallback 并行聚合
 * 2）anyOfOrFallback 主备快速返回
 * 3）retry 指数退避
 * 使用示例：见 Controller
 * 注意事项：下游调用需幂等；降级值类型一致
 * 入参与出参与异常说明：详见各方法
 */
@Service
public class DemoService {
    private final CfEngine engine;
    public DemoService(CfEngine engine) { this.engine = engine; }

    public CompletableFuture<List<String>> aggregate(String id) {
        MDC.put("traceId", UUID.randomUUID().toString());
        List<java.util.function.Supplier<String>> tasks = Arrays.<java.util.function.Supplier<String>>asList(
                new java.util.function.Supplier<String>() {
                    public String get() { return engine.retry(2, Backoff.expo(50, 200, TimeUnit.MILLISECONDS), new java.util.function.Supplier<String>() {
                        public String get() { return callA(id); }
                    }).join(); }
                },
                new java.util.function.Supplier<String>() { public String get() { return callB(id); } },
                new java.util.function.Supplier<String>() { public String get() { return callC(id); } }
        );
        return engine.allWithFallback(tasks, new java.util.function.Function<Throwable, String>() {
            public String apply(Throwable ex) { return "fallback"; }
        }, 220, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<String> fastReplica(final String key) {
        List<java.util.function.Supplier<String>> replicas = Arrays.<java.util.function.Supplier<String>>asList(
                new java.util.function.Supplier<String>() { public String get() { return fromAz("az1", key); } },
                new java.util.function.Supplier<String>() { public String get() { return fromAz("az2", key); } }
        );
        return engine.anyOfOrFallback(replicas, "fallback", 150, TimeUnit.MILLISECONDS);
    }

    private static String callA(String id) { sleep(60); return "A:" + id; }
    private static String callB(String id) { sleep(120); return "B:" + id; }
    private static String callC(String id) { if ((id.hashCode() & 1) == 0) throw new RuntimeException("boom"); return "C:" + id; }
    private static String fromAz(String az, String key) { sleep(80); return az + ":" + key; }
    private static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } }
}
