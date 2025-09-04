package com.st.modules.thread.framework.v2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 概述：装配融合后的 Orchestrator 与 DispatcherAdapter
 * 功能清单：提供可注入的编排与调度器 Bean
 * 使用示例：注入 OrchestratorServiceV2 使用
 * 注意事项：CfEngine 建议用我之前给出的 CfAutoConfig 进行属性化
 * 入参与出参与异常说明：无
 */
@Configuration
public class FusionConfig {
    @Bean
    public DispatcherAdapter dispatcherAdapter(CfEngine engine) { return new DispatcherAdapter(engine); }

    @Bean
    public OrchestratorServiceV2 orchestratorServiceV2(CoreService coreService, DispatcherAdapter dispatcherAdapter, CfEngine engine) {
        return new OrchestratorServiceV2(coreService, dispatcherAdapter, engine);
    }
}
