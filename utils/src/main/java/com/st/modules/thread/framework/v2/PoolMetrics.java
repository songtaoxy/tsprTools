package com.st.modules.thread.framework.v2;


/**
 * 概述：线程池指标快照对象
 * 功能清单：记录常见线程池运行指标
 * 使用示例：engine.snapshot()
 * 注意事项：仅为快照，不持久化
 * 入参与出参与异常说明：构造入参为各指标；无异常
 */
public class PoolMetrics {
    public final int corePoolSize;
    public final int maxPoolSize;
    public final int poolSize;
    public final int activeCount;
    public final int largestPoolSize;
    public final long taskCount;
    public final long completedTaskCount;
    public final int queueSize;
    public final int queueRemainingCapacity;

    public PoolMetrics(int corePoolSize, int maxPoolSize, int poolSize, int activeCount, int largestPoolSize,
                       long taskCount, long completedTaskCount, int queueSize, int queueRemainingCapacity) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.poolSize = poolSize;
        this.activeCount = activeCount;
        this.largestPoolSize = largestPoolSize;
        this.taskCount = taskCount;
        this.completedTaskCount = completedTaskCount;
        this.queueSize = queueSize;
        this.queueRemainingCapacity = queueRemainingCapacity;
    }
}
