package com.st.modules.thread.framework.v2.demo;


import com.st.modules.thread.framework.v2.CfEngine;
import com.st.modules.thread.framework.v2.PoolMetrics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * 概述：根据等待阈值决定同步或异步返回，异步返回任务票据并提供查询接口
 * 功能清单：
 * 1）同步路径：在阈值内完成直接返回
 * 2）异步路径：超时即返回 202 与 taskId；后台完成后可查询
 * 3）暴露线程池指标
 * 使用示例：GET /api/agg?id=1&waitMs=120；GET /api/agg/{taskId}
 * 注意事项：示例用内存 Map 保存结果，生产替换为 DB 或缓存
 * 入参与出参与异常说明：超时走票据；异常返回 500
 */
@RestController
@RequestMapping("/api")
public class DemoController {
    private final DemoService service;
    private final CfEngine engine;
    private final ConcurrentMap<String, Object> store = new ConcurrentHashMap<String, Object>();

    public DemoController(DemoService service, CfEngine engine) {
        this.service = service;
        this.engine = engine;
    }

    @GetMapping("/agg")
    public ResponseEntity<?> aggregate(@RequestParam("id") String id,
                                       @RequestParam(value = "waitMs", defaultValue = "120") long waitMs) {
        CompletableFuture<java.util.List<String>> cf = service.aggregate(id);
        try {
            java.util.List<String> data = cf.get(waitMs, TimeUnit.MILLISECONDS);
            return ResponseEntity.ok(data);
        } catch (TimeoutException te) {
            final String taskId = UUID.randomUUID().toString();
            cf.whenComplete(new java.util.function.BiConsumer<java.util.List<String>, Throwable>() {
                public void accept(java.util.List<String> ret, Throwable err) {
                    store.put(taskId, err == null ? ret : java.util.Arrays.asList("fallback"));
                }
            });
            Map<String, Object> ticket = new HashMap<String, Object>();
            ticket.put("taskId", taskId);
            ticket.put("status", "processing");
            return ResponseEntity.accepted().body(ticket);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("internal error");
        }
    }

    @GetMapping("/agg/{taskId}")
    public ResponseEntity<?> query(@PathVariable("taskId") String taskId) {
        Object v = store.get(taskId);
        if (v == null) return ResponseEntity.status(202).body("processing");
        return ResponseEntity.ok(v);
    }

    @GetMapping("/pool/metrics")
    public PoolMetrics metrics() { return engine.snapshot(); }
}

