package com.st.modules.thread.framework.v3;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 简易管理端点：指标快照 + 运行时调整核心/最大线程数 */
@RestController
@RequestMapping("/engine")
public class EngineAdminController {
    private final Engine engine;
    public EngineAdminController(Engine engine) { this.engine = engine; }

    @GetMapping("/metrics")
    public ResponseEntity<PoolMetrics> metrics() { return ResponseEntity.ok(engine.snapshot()); }

    @PostMapping("/resize")
    public ResponseEntity<String> resize(@RequestParam("core") int core, @RequestParam("max") int max) {
        engine.resize(core, max);
        return ResponseEntity.ok("ok");
    }
}
