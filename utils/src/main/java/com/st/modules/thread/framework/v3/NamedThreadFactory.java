package com.st.modules.thread.framework.v3;


import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** 统一命名与未捕获异常处理 */
public final class NamedThreadFactory implements ThreadFactory {
    private final String prefix;
    private final ThreadFactory delegate = Executors.defaultThreadFactory();
    private final AtomicInteger seq = new AtomicInteger(1);

    public NamedThreadFactory(String prefix) { this.prefix = prefix; }

    @Override public Thread newThread(Runnable r) {
        Thread t = delegate.newThread(r);
        t.setName(prefix + "-" + seq.getAndIncrement());
        t.setDaemon(false);
        t.setUncaughtExceptionHandler((th, ex) ->
                System.err.println("[Engine] uncaught in " + th.getName() + ": " + ex));
        return t;
    }
}

