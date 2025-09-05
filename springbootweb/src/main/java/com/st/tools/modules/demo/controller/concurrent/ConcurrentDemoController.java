package com.st.tools.modules.demo.controller.concurrent;

import com.st.tools.common.response.Response;
import com.st.modules.thread.framework.v3.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

/**
 * 演示控制器
 * - /api/agg   并行聚合：?names=a&names=b&timeoutMs=500
 * - /api/any   快速副本：?key=k1&timeoutMs=150
 * - /api/task  提交任务（同步优先，超时转票据）
 * - /api/task/{id}  查询票据结果
 *
 * 依赖：
 * - OrchestratorService 已在 EngineAutoConfiguration 中装配为 Bean
 * - 已接入 Micrometer + Prometheus 后，可在 /actuator/prometheus 查看指标
 */
@RestController
@RequestMapping("/api")
public class ConcurrentDemoController {

    private final OrchestratorService orchestrator;

    public ConcurrentDemoController(OrchestratorService orchestrator) {
        this.orchestrator = orchestrator;
    }

    /**
     * 并行聚合：对 names 列表并发执行，单项失败用 fallback 占位；全局超时由 timeoutMs 控制
     * 示例：GET /api/agg?names=a&names=b&timeoutMs=500
     */
    @GetMapping("/agg")
    public Response<List<String>> aggregate(
            @RequestParam("names") List<String> names,
            @RequestParam(value = "timeoutMs", defaultValue = "500")  long timeoutMs) {
        List<String> out = orchestrator.aggregate(names, timeoutMs);
        return Response.ok(out);
    }

    /**
     * 快速副本：多活/主备，先完成者返回；整体超时 timeoutMs，到点用 fallback
     * 示例：GET /api/any?key=foo&timeoutMs=150
     */
    @GetMapping("/any")
    public ResponseEntity<Map<String, Object>> any(
            @RequestParam("key")  String key,
            @RequestParam(value = "timeoutMs", defaultValue = "150")  long timeoutMs) {
        String data = orchestrator.fastReplica(key, timeoutMs);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("key", key);
        body.put("data", data);
        return ResponseEntity.ok(body);
    }

    /**
     * 提交任务：同步等待 waitMs；若超时，返回 {taskId, status: PROCESSING} 作为票据
     * 示例：POST /api/task  {"name":"job-1","waitMs":120}
     */
    @PostMapping("/task")
    public ResponseEntity<Map<String, Object>> submitTask(@RequestBody SubmitTaskRequest req) {
        long wait = req.waitMs != null ? req.waitMs : 120L;
        Map<String, Object> result = orchestrator.submitWithTicket(req.name, wait);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询票据：若后台已完成，返回最终数据；否则返回 "PROCESSING"
     * 示例：GET /api/task/{id}
     */
    @GetMapping("/task/{id}")
    public ResponseEntity<Map<String, Object>> queryTask(@PathVariable("id") String taskId) {
        Object data = orchestrator.queryTicket(taskId);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("taskId", taskId);
        body.put("data", data);
        return ResponseEntity.ok(body);
    }

    /* ---------- 请求体 DTO ---------- */

    public static class SubmitTaskRequest {

        public String name;


        public Long waitMs;

        public SubmitTaskRequest() {}
        public SubmitTaskRequest(String name, Long waitMs) { this.name = name; this.waitMs = waitMs; }
        public String getName() { return name; }
        public Long getWaitMs() { return waitMs; }
        public void setName(String name) { this.name = name; }
        public void setWaitMs(Long waitMs) { this.waitMs = waitMs; }
    }
}
