package com.st.modules.thread.framework.v2;



import java.util.concurrent.Executor;

/**
 * 概述：Executor 适配器, 将 CfEngine 的提交能力暴露为 Executor 接口
 * 功能清单：兼容 v1 代码中依赖 Executor 的调用点
 * 使用示例：Executor ex = new EngineExecutor(engine)
 * 注意事项：仅用于过渡；新代码直接用 CfEngine API
 * 入参与出参与异常说明：无
 */
public class EngineExecutor implements Executor {
    private final CfEngine engine;
    public EngineExecutor(CfEngine engine) { this.engine = engine; }
    public void execute(Runnable command) {
        engine.supply(new java.util.function.Supplier<Object>() {
            public Object get() { command.run(); return null; }
        },  Long.MAX_VALUE, java.util.concurrent.TimeUnit.DAYS, new java.util.function.Supplier<Object>() {
            public Object get() { return null; }
        });
    }
}
