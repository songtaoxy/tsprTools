package com.st.modules.thread.framework.v2;


import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 概述：MDC 上下文透传工具
 * 功能清单：
 * 1）包装 Runnable Callable 保证提交时的 MDC 在线程执行中可见
 * 2）执行后还原父线程 MDC
 * 使用示例：MdcPropagator.wrap(runnable, MDC.getCopyOfContextMap())
 * 注意事项：仅处理 MDC；如需自定义 ThreadLocal 可仿照扩展
 * 入参与出参与异常说明：包装器不改变任务异常传播
 */
public final class MdcPropagator {
    private MdcPropagator() {}

    public static Runnable wrap(Runnable task, Map<String, String> mdc) {
        return () -> {
            Map<String, String> old = MDC.getCopyOfContextMap();
            try { set(mdc); task.run(); }
            finally { set(old); }
        };
    }

    public static <V> Callable<V> wrap(Callable<V> task, Map<String, String> mdc) {
        return () -> {
            Map<String, String> old = MDC.getCopyOfContextMap();
            try { set(mdc); return task.call(); }
            finally { set(old); }
        };
    }

    private static void set(Map<String, String> ctx) {
        if (ctx == null) MDC.clear(); else MDC.setContextMap(ctx);
    }
}
