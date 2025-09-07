package com.st.modules.thread.framework.v4;


import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/** 常用包装器：MDC + BizContext 捕获并恢复 */
public final class TaskWrappers {
    private TaskWrappers(){}

    public static TaskWrapper bizAndMdcCapture() {
        return new TaskWrapper() {
            public Runnable wrap(final Runnable r) {
                final Map<String,String> parentMdc = MDC.getCopyOfContextMap();
                final BizContext parentCtx = BizContextHolder.get();
                return new Runnable() {
                    public void run() {
                        Map<String,String> old = MDC.getCopyOfContextMap();
                        if (parentMdc != null) MDC.setContextMap(parentMdc); else MDC.clear();
                        // try ( … ) { … } 是 try-with-resources 语法；块结束会自动调用 close()
                        // 核心是 try-with-resources + AutoCloseable Scope
                        try (BizContextHolder.Scope scope = BizContextHolder.with(parentCtx)) {
                            // 清除可能遗留的中断标志，避免跨任务泄漏
                            Thread.interrupted();
                            r.run();
                        } finally {
                            if (old != null) MDC.setContextMap(old); else MDC.clear();
                        }
                    }
                };
            }

            public <T> Callable<T> wrap(final Callable<T> c) {
                final Map<String,String> parentMdc = MDC.getCopyOfContextMap();
                final BizContext parentCtx = BizContextHolder.get();
                return new Callable<T>() {
                    public T call() throws Exception {
                        Map<String,String> old = MDC.getCopyOfContextMap();
                        if (parentMdc != null) MDC.setContextMap(parentMdc); else MDC.clear();
                        // try ( … ) { … } 是 try-with-resources 语法；块结束会自动调用 close()
                        // 核心是 try-with-resources + AutoCloseable Scope
                        try (BizContextHolder.Scope scope = BizContextHolder.with(parentCtx)) {
                            Thread.interrupted();
                            return c.call();
                        } finally {
                            if (old != null) MDC.setContextMap(old); else MDC.clear();
                        }
                    }
                };
            }
        };
    }
}
