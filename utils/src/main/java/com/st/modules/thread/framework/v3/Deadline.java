package com.st.modules.thread.framework.v3;


import java.util.concurrent.TimeUnit;

/** 全局截止时间（Deadline）工具：用于分步编排时传递剩余时长 */
public final class Deadline {
    private final long deadlineNanos;
    private Deadline(long ddlNanos) { this.deadlineNanos = ddlNanos; }

    public static Deadline after(long timeout, TimeUnit unit) {
        return new Deadline(System.nanoTime() + unit.toNanos(timeout));
    }

    /** 剩余毫秒（<=0 代表到了） */
    public long leftMillis() {
        long left = deadlineNanos - System.nanoTime();
        return left <= 0 ? 0 : TimeUnit.NANOSECONDS.toMillis(left);
    }

    public boolean isExpired() { return leftMillis() <= 0; }
}

