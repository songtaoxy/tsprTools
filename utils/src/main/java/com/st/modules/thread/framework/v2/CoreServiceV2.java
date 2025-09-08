package com.st.modules.thread.framework.v2;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

/**
 * 概述：核心业务服务，仅处理业务逻辑
 * 功能清单：handleBusiness 泛型入参，返回结果对象
 * 使用示例：service.handleBusiness(input, ctx.getMetadata())
 * 注意事项：保持幂等；内部异常向上抛出由编排层处理
 * 入参与出参与异常说明：入参为泛型对象与元数据；异常透传
 */

@Service
public class CoreServiceV2 {
    public <I, O> O handleBusiness(I input, ObjectNode metadata) {
        // 这里写你的真实业务逻辑，示例为直接返回 toString
        return (O) String.valueOf(input);
    }
}
