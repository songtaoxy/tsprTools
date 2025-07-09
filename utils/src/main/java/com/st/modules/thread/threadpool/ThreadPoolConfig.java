package com.st.modules.thread.threadpool;

public class ThreadPoolConfig {
    public int corePoolSize = 4;
    public int maxPoolSize = 10;
    public int queueCapacity = 100;
    public long keepAliveSeconds = 60L;

    public ThreadPoolConfig() {}

    public ThreadPoolConfig(int core, int max, int queue, long keepAliveSeconds) {
        this.corePoolSize = core;
        this.maxPoolSize = max;
        this.queueCapacity = queue;
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public static ThreadPoolConfig of(int core, int max, int queue, long keepAliveSeconds) {
        return new ThreadPoolConfig(core, max, queue, keepAliveSeconds);
    }
}

