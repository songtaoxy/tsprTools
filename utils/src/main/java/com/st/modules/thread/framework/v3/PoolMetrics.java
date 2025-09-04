package com.st.modules.thread.framework.v3;

/** 线程池指标快照 */
public final class PoolMetrics {
    public final int corePoolSize, maxPoolSize, poolSize, activeCount, largestPoolSize;
    public final long taskCount, completedTaskCount;
    public final int queueSize, queueRemainingCapacity;

    public PoolMetrics(int corePoolSize, int maxPoolSize, int poolSize, int activeCount, int largestPoolSize,
                       long taskCount, long completedTaskCount, int queueSize, int queueRemainingCapacity) {
        this.corePoolSize = corePoolSize; this.maxPoolSize = maxPoolSize; this.poolSize = poolSize;
        this.activeCount = activeCount; this.largestPoolSize = largestPoolSize;
        this.taskCount = taskCount; this.completedTaskCount = completedTaskCount;
        this.queueSize = queueSize; this.queueRemainingCapacity = queueRemainingCapacity;
    }
}

