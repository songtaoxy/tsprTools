package com.st.tools.modules.demo.controller.concurrent;

import com.st.tools.common.response.Response;
import com.st.modules.thread.framework.v3.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
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
     * <pre>
     *     - 场景: 多任务并发执行 → Controller 只等 waitMs；
     *     能等到就同步返回；等不到就立刻返回票据。
     *     后台继续跑到“全局软截止时间（soft deadline）”，
     *     到点还没完成的子任务一律用 fallback 收尾，整单不标记失败
     * </pre>
     *
     * <pre>
     *     具体说明:
     *     - 每个子任务都有自己的超时 + fallback（有损）；
     *     - 还设置一个全局软截止：到点不失败，而是把未完成的子任务直接置为 fallback，并完成整单；
     *     - Controller 同步窗口 waitMs 内没拿到结果 ⇒ 立即返回票据，后台按上面规则收尾；
     *     - 进度/失败明细通过 Ticket + SSE（沿用你前面已经接的 TicketStore/Redis 版）。
     * </pre>
     * @param req
     * @return
     */
    @PostMapping("/multi-smart-soft")
    public ResponseEntity<Map<String,Object>> multiSmartSoft(@RequestBody MultiSmartReq req) {
        long wait   = req.waitMs != null ? req.waitMs : 120L;   // 同步窗口
        long stepTO = req.perTaskTimeoutMs != null ? req.perTaskTimeoutMs : 150L; // 子任务超时（到点fallback）
        long soft   = req.globalSoftMs != null ? req.globalSoftMs : 400L; // 全局软截止（不失败）
        Map<String,Object> res = orchestrator.multiSmartAggregateSoftNoFail(req.names, wait, stepTO, soft);
        return ResponseEntity.ok(res);
    }


    /**
     * <pre>
     *     多个任务，controller等待一段时间，完成则同步返回；
     *     否则异步返回；全局超时 不失败；
     *     对于任意子任务，真的失败则占位，没有失败则不受全局超时控制，继续执行直到结束
     * </pre>
     * @param req
     * @return
     */
    @PostMapping("/multi-smart-open")
    public ResponseEntity<Map<String,Object>> multiSmartOpen(@RequestBody MultiSmartReq req) {
        long wait   = req.waitMs != null ? req.waitMs : 120L;     // 同步窗口
        Long stepTO = req.perTaskTimeoutMs;                       // 可为 null：表示不设子任务超时
        Map<String,Object> res = orchestrator.multiSmartAggregateOpenEnded(req.names, wait, stepTO);
        return ResponseEntity.ok(res);
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

    public static class MultiSmartReq {
        @NotEmpty
        public List<String> names;
        @Min(1)  public Long waitMs;
        @Min(1)  public Long perTaskTimeoutMs;
        @Min(1)  public Long globalSoftMs;
        // getters/setters 省略
    }


}


