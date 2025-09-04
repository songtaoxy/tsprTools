package com.st.modules.thread.framework.v3;

import org.slf4j.MDC;
import java.util.Map;
import java.util.concurrent.Callable;

/** 简洁的 MDC 透传（可按此模式扩展自定义 ThreadLocal） */
public final class MdcPropagator {
    private MdcPropagator() {}
    public static Runnable wrap(Runnable task, Map<String,String> mdc) {
        return () -> {
            Map<String,String> old = MDC.getCopyOfContextMap();
            try { if (mdc == null) MDC.clear(); else MDC.setContextMap(mdc); task.run(); }
            finally { if (old == null) MDC.clear(); else MDC.setContextMap(old); }
        };
    }
    public static <V> Callable<V> wrap(Callable<V> task, Map<String,String> mdc) {
        return () -> {
            Map<String,String> old = MDC.getCopyOfContextMap();
            try { if (mdc == null) MDC.clear(); else MDC.setContextMap(mdc); return task.call(); }
            finally { if (old == null) MDC.clear(); else MDC.setContextMap(old); }
        };
    }
}

