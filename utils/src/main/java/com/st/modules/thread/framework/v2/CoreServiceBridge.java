package com.st.modules.thread.framework.v2;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.st.modules.thread.framework.v2.CoreService;

/**
 * 概述：CoreService 桥接, 保持 v1 签名兼容
 * 功能清单：提供 v1 风格的 handleBusiness 重载, 内部委托给 v2 通用实现
 * 使用示例：bridge.handleBusiness(taskId, input, meta)
 * 注意事项：确保幂等
 * 入参与出参与异常说明：与 v1 对齐
 */
public class CoreServiceBridge extends CoreService {
    public <T> String handleBusiness(String taskId, T input, ObjectNode meta) {
        meta.put("taskId", taskId);
        Object out = super.handleBusiness(input, meta);
        return out == null ? null : out.toString();
    }
}
