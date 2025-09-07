package com.st.tools.common.config.concurrent;


import com.st.modules.thread.framework.v4.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineAutoConfig {

    @Bean
    public Engine engine() {
        return Engine.newBuilder()
                .poolName("biz")
                .core(Math.max(2, Runtime.getRuntime().availableProcessors()))
                .max(Math.max(4, Runtime.getRuntime().availableProcessors()*2))
                .queue(256)
                .keepAliveSeconds(60)
                .taskWrapper(TaskWrappers.bizAndMdcCapture())
                .build();
    }

    @Bean public TicketStore ticketStore(){ return new InMemoryTicketStore(); }

    @Bean public OrchestratorService orchestratorService(Engine engine, TicketStore store){
        return new OrchestratorService(engine, store);
    }
}

