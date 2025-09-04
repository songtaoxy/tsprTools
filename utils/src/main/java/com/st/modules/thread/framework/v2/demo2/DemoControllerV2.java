package com.st.modules.thread.framework.v2.demo2;

import com.st.modules.thread.framework.v2.OrchestratorServiceV2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 概述：对外接口层，演示同步优先与票据查询
 * 功能清单：
 * 1）/v2/agg 并行聚合
 * 2）/v2/any 主备快速返回
 * 3）/v2/task 提交任务 同步等待waitMs 超时返回票据
 * 4）/v2/task/{id} 查询票据
 * 使用示例：见各接口
 * 注意事项：生产环境请挂接鉴权与限流
 * 入参与出参与异常说明：无检查异常
 */
@RestController
@RequestMapping("/v2")
public class DemoControllerV2 {
    private final OrchestratorServiceV2 orc;

    public DemoControllerV2(OrchestratorServiceV2 orc) { this.orc = orc; }

    @GetMapping("/agg")
    public ResponseEntity<?> agg(@RequestParam("names") List<String> names,
                                 @RequestParam(value = "timeoutMs", defaultValue = "220") long timeoutMs) {
        return ResponseEntity.ok(orc.processAll(names, timeoutMs));
    }

    @GetMapping("/any")
    public ResponseEntity<?> any(@RequestParam("key") String key,
                                 @RequestParam(value = "timeoutMs", defaultValue = "150") long timeoutMs) {
        return ResponseEntity.ok(orc.processAny(key, timeoutMs));
    }

    @PostMapping("/task")
    public ResponseEntity<?> submit(@RequestParam("name") String name,
                                    @RequestParam(value = "waitMs", defaultValue = "120") long waitMs) {
        return ResponseEntity.ok(orc.processWithTicket(name, waitMs));
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<?> query(@PathVariable("id") String id) {
        return ResponseEntity.ok(orc.queryTicket(id));
    }
}

