package com.st.modules.thread.framework.v4.core;


import java.util.concurrent.Callable;

/** 提交前包装任务（透传上下文/MDC、统一打点等） */
public interface TaskWrapper {
    Runnable wrap(Runnable r);
    <T> Callable<T> wrap(Callable<T> c);

    /** 组合：先 self.wrap，再 next.wrap */
    default TaskWrapper andThen(final TaskWrapper next){
        final TaskWrapper self = this;
        return new TaskWrapper() {
            public Runnable wrap(Runnable r){ return next.wrap(self.wrap(r)); }
            public <T> Callable<T> wrap(Callable<T> c){ return next.wrap(self.wrap(c)); }
        };
    }

    TaskWrapper NOOP = new TaskWrapper() {
        public Runnable wrap(Runnable r){ return r; }
        public <T> Callable<T> wrap(Callable<T> c){ return c; }
    };
}
