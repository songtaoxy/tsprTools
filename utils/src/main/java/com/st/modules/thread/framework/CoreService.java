package com.st.modules.thread.framework;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class CoreService {
    @SneakyThrows
    public <T> String handleBusiness(String taskId, T input, ObjectNode meta) {
        // 仅关注业务逻辑
        Thread.sleep(1000);
        return "任务["+taskId+"]处理完成: " + input.toString();
    }
}
