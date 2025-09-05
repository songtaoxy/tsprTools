package com.st.modules.thread.framework.v3;


import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface TicketStore {
    void save(String taskId, Object value);
    Object get(String taskId);

    default void saveTicket(Ticket t) { save(t.taskId, t); }
    default Ticket getTicket(String taskId) { return (Ticket) get(taskId); }

    /* SSE（本机内存注册） */
    void registerEmitter(String taskId, SseEmitter emitter);

    /* 广播最新变更（本机 & 跨实例） */
    void publishUpdate(String taskId, Ticket t);
}
