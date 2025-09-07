package com.st.modules.thread.framework.v4;


import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 票据：一次并发聚合请求的全生命周期状态 */
public final class Ticket implements Serializable {

    public enum Status { INIT, PROCESSING, SUCCESS, FAILED }
    public enum Stage { PREPARE(10), CALL_DOWNSTREAM(60), MERGE(20), FINISH(10);
        public final int weight; Stage(int w){ this.weight=w; } }

    public static final class Subtask implements Serializable {
        public final String name;
        public volatile String status = "INIT"; // INIT/RUNNING/SUCCESS/FALLBACK/FAILED
        public volatile int progress = 0;       // 0..100
        public volatile String error;
        public volatile Stage stage = Stage.PREPARE;
        public final long startedAt = System.currentTimeMillis();
        public volatile long endedAt = 0L;
        public Subtask(String name){ this.name=name; }
    }

    public final String taskId;
    public volatile String mode = "ASYNC";
    public volatile Status status = Status.INIT;
    public volatile int progress = 0;
    public final Map<String,Subtask> subtasks = new ConcurrentHashMap<String,Subtask>();
    public volatile Object data;
    public volatile String error;

    public final long startedAt = System.currentTimeMillis();
    public volatile long updatedAt = System.currentTimeMillis();
    public volatile boolean strictAll = false;

    // 聚合统计
    public volatile int okCount=0, fallbackCount=0, failedCount=0, totalCount=0;

    public Ticket(String taskId, List<String> names, boolean strictAll){
        this.taskId=taskId; this.strictAll=strictAll;
        for (String n: names) subtasks.put(n, new Subtask(n));
        this.totalCount = names.size();
    }

    public void touch(){ this.updatedAt = System.currentTimeMillis(); }

    public static void advanceStage(Subtask s, Stage next){
        int acc;
        switch (next) {
            case PREPARE:
                acc = Stage.PREPARE.weight; break;
            case CALL_DOWNSTREAM:
                acc = Stage.PREPARE.weight + Stage.CALL_DOWNSTREAM.weight; break;
            case MERGE:
                acc = Stage.PREPARE.weight + Stage.CALL_DOWNSTREAM.weight + Stage.MERGE.weight; break;
            case FINISH:
            default:
                acc = 100; break;
        }
        s.stage = next; s.progress = Math.min(100, acc);
    }

    /** 统一重算整体进度与统计（每次子任务状态变更后调用） */
    public void recomputeAggregates(){
        int sum=0, ok=0, fb=0, fail=0, total=subtasks.size();
        for (Subtask s : subtasks.values()) {
            sum += s.progress;
            if ("SUCCESS".equals(s.status)) ok++;
            else if ("FALLBACK".equals(s.status)) fb++;
            else if ("FAILED".equals(s.status)) fail++;
        }
        this.progress = (total==0) ? 0 : Math.min(100, sum / total);
        this.okCount=ok; this.fallbackCount=fb; this.failedCount=fail; this.totalCount=total;
        touch();
    }
}
