package com.st.modules.thread.framework.v2;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 概述：命名线程工厂，统一线程命名与未捕获异常处理
 * 功能清单：
 * 1）按前缀命名线程
 * 2）绑定 UncaughtExceptionHandler
 * 使用示例：new NamedThreadFactory("biz-pool")
 * 注意事项：生产建议统一命名规范，便于日志检索
 * 入参与出参与异常说明：构造入参为前缀字符串；无特殊异常
 */
public class NamedThreadFactory implements ThreadFactory {
    private final String prefix;
    private final ThreadFactory delegate = Executors.defaultThreadFactory();
    private final AtomicInteger seq = new AtomicInteger(1);

    public NamedThreadFactory(String prefix) { this.prefix = prefix; }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = delegate.newThread(r);
        t.setName(prefix + "-" + seq.getAndIncrement());
        t.setDaemon(false);
        t.setUncaughtExceptionHandler((th, ex) ->
                System.err.println("uncaught in " + th.getName() + ": " + ex));
        return t;
    }
}
