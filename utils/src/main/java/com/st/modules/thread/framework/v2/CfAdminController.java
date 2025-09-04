package com.st.modules.thread.framework.v2;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 概述：简易管理端点，查看线程池指标
 * 功能清单：GET /concurrent/metrics
 * 使用示例：curl http://localhost:8080/concurrent/metrics
 * 注意事项：生产可挂到鉴权体系
 * 入参与出参与异常说明：无
 */
@RestController
public class CfAdminController {
    private final CfEngine engine;
    public CfAdminController(CfEngine engine) { this.engine = engine; }

    @GetMapping("/concurrent/metrics")
    public ResponseEntity<PoolMetrics> metrics() {
        return ResponseEntity.ok(engine.snapshot());
    }
}
