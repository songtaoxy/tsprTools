package com.st.modules.thread.framework.v4.orchestration;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 票据：一次并发聚合请求的全生命周期状态 */

import java.util.concurrent.atomic.AtomicInteger;

/** 票据对象：聚合任务的对外可视化快照 */
public class Ticket {

    // ----- 枚举与常量 -----
    public enum Status { PROCESSING, SUCCESS, FAILED, CANCELLED }
    public enum Mode { SYNC, ASYNC }

    /** 子任务阶段（用于对外展示与粗略进度） */
    public enum Stage {
        INIT(0),
        PREPARE(10),
        CALL_DOWNSTREAM(70),
        MERGE(90),
        FINISH(100);

        public final int weight;
        Stage(int w){ this.weight = w; }
    }

    // ----- 顶层字段 -----
    public final String taskId;
    public Mode mode = Mode.ASYNC;

    public volatile Status status = Status.PROCESSING;
    public volatile int progress = 0;                // 0~100

    public final long createdAt = System.currentTimeMillis();
    public volatile long updatedAt = this.createdAt;
    public volatile String error;                    // 整体错误（严格模式）

    // 统计（由 recomputeAggregates() 统一计算）
    public volatile int totalCount = 0;
    public volatile int okCount = 0;
    public volatile int fallbackCount = 0;
    public volatile int failedCount = 0;

    /** 所有子任务（键：子任务名） */
    public final Map<String, Subtask> subtasks = new ConcurrentHashMap<>();

    /** 可选：对外返回的数据聚合（控制器可能直接返回） */
    public final Map<String,Object> data = new ConcurrentHashMap<>();

    // ----- 构造 -----
    public Ticket(String taskId, Collection<String> names, boolean strictAll) {
        this.taskId = taskId;
        if (names != null) {
            for (String n : names) {
                subtasks.put(n, new Subtask(n));
            }
        }
        this.totalCount = subtasks.size();
        touch();
    }

    public void touch(){ this.updatedAt = System.currentTimeMillis(); }

    // ----- 子任务模型 -----
    public static class Subtask {
        public final String name;

        /** 运行状态：INIT / RUNNING / SUCCESS / FALLBACK / FAILED / CANCELLED */
        public volatile String status = "INIT";

        /** 0~100 的阶段进度（粗略） */
        public final AtomicInteger progress = new AtomicInteger(0);

        /** 最近一次错误（仅 FALLBACK/FAILED/CANCELLED 时有值） */
        public volatile String error;

        /** 阶段枚举（用于可视化时间线） */
        public volatile Stage stage = Stage.INIT;

        /** 时间戳：开始/结束 */
        public volatile long startedAt;
        public volatile long endedAt;

        /** 承载阶段性业务元信息（如 path、下游 reqId、资源位 id 等） */
        public Map<String,Object> meta = new ConcurrentHashMap<>();

        public Subtask(String name){ this.name = name; }

        public boolean isDone() {
            return "SUCCESS".equals(status) || "FALLBACK".equals(status) ||
                    "FAILED".equals(status)  || "CANCELLED".equals(status);
        }
    }

    // ----- 工具：推进阶段并同步粗略进度 -----
    public static void advanceStage(Subtask st, Stage stage) {
        if (st == null || stage == null) return;
        st.stage = stage;
        int p = Math.max(st.progress.get(), stage.weight);
        st.progress.set(Math.min(100, p));
    }

    // ----- 聚合统计：统一计算 ok/fallback/failed/progress -----
    public void recomputeAggregates() {
        int ok = 0, fb = 0, fail = 0, done = 0;
        int localTotal = subtasks.size();

        for (Subtask s : subtasks.values()) {
            if ("SUCCESS".equals(s.status)) { ok++; done++; }
            else if ("FALLBACK".equals(s.status)) { fb++; done++; }
            else if ("FAILED".equals(s.status) || "CANCELLED".equals(s.status)) { fail++; done++; }
        }
        this.totalCount = localTotal;
        this.okCount = ok;
        this.fallbackCount = fb;
        this.failedCount = fail;

        // 整体进度：按“已结束的子任务 / 总数”
        if (localTotal <= 0) this.progress = 100;
        else this.progress = Math.min(100, (int) Math.floor(done * 100.0 / localTotal));
        touch();
    }


    public void setItems(String key, Object value) {
        this.data.clear();
        this.data.put(key, value);
    }
}

