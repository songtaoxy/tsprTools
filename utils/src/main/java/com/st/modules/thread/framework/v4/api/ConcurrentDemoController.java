package com.st.modules.thread.framework.v4.api;



import com.st.modules.thread.framework.v4.context.BizContext;
import com.st.modules.thread.framework.v4.context.CurrentBizContext;
import com.st.modules.thread.framework.v4.orchestration.OrchestratorService;
import com.st.modules.thread.framework.v4.orchestration.Ticket;
import com.st.modules.thread.framework.v4.orchestration.TicketStore;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ConcurrentDemoController {

    private final OrchestratorService orchestrator;
    private final TicketStore store;

    public ConcurrentDemoController(OrchestratorService orchestrator, TicketStore store) {
        this.orchestrator = orchestrator; this.store = store;
    }

    /* —— 开放式有损聚合 —— */
    @PostMapping("/multi-smart-open")
    public ResponseEntity<Map<String,Object>> multiSmartOpen(@RequestBody MultiReq req, @CurrentBizContext BizContext ctx) {
        long wait   = req.waitMs != null ? req.waitMs : 120L;
        Long stepTO = req.perTaskTimeoutMs; // 可为 null 表示无步超时
        ctx.put("req.names", req.names);
        Map<String,Object> res = orchestrator.multiSmartAggregateOpenEnded(req.names, wait, stepTO);
        return ResponseEntity.ok(res);
    }

    /* —— 全局软截止（不失败） —— */
    @PostMapping("/multi-smart-soft")
    public ResponseEntity<Map<String,Object>> multiSmartSoft(@RequestBody SoftReq req) {
        long wait = req.waitMs==null?120L:req.waitMs;
        long step = req.perTaskTimeoutMs==null?150L:req.perTaskTimeoutMs;
        long soft = req.globalSoftMs==null?400L:req.globalSoftMs;
        return ResponseEntity.ok(orchestrator.multiSmartAggregateSoftNoFail(req.names, wait, step, soft));
    }

    /* —— 严格模式（fail-fast） —— */
    @PostMapping("/all-failfast")
    public ResponseEntity<Map<String,Object>> allFailFast(@RequestBody HardReq req) {
        long wait = req.waitMs==null?200L:req.waitMs;
        long step = req.perTaskTimeoutMs==null?150L:req.perTaskTimeoutMs;
        return ResponseEntity.ok(orchestrator.allFailFastAggregate(req.names, wait, step));
    }

    /* —— 票据查询 —— */
    @GetMapping("/task/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable("id") String id) {
        Ticket t = store.getTicket(id);
        if (t == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(t);
    }

    /* —— SSE 订阅进度 —— */
    @GetMapping(path="/task/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable("id") String id) throws IOException {
        final SseEmitter emitter = new SseEmitter(0L);
        final java.util.function.Consumer<Ticket> listener = new java.util.function.Consumer<Ticket>() {
            public void accept(Ticket t) {
                try { emitter.send(SseEmitter.event().name("progress").data(t)); }
                catch (IOException e) { emitter.completeWithError(e); }
            }
        };
        store.subscribe(id, listener);
        emitter.onCompletion(new Runnable(){ public void run(){ store.unsubscribe(id, listener); }});
        emitter.onTimeout(new Runnable(){ public void run(){ store.unsubscribe(id, listener); }});
        Ticket cur = store.getTicket(id); if (cur!=null) listener.accept(cur); // 首帧
        return emitter;
    }

    /* —— 请求体 DTO —— */
    public static class MultiReq {
        @NotEmpty public List<String> names;
        @Min(1)  public Long waitMs;
        @Min(1)  public Long perTaskTimeoutMs; // null 表示无步超时
    }
    public static class SoftReq extends MultiReq { @Min(1) public Long globalSoftMs; }
    public static class HardReq {
        @NotEmpty public List<String> names; @Min(1) public Long waitMs; @Min(1) public Long perTaskTimeoutMs;
    }
}
