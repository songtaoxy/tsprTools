package com.st.modules.thread.framework.v3;

public interface TicketStore {
    void save(String taskId, Object value);
    Object get(String taskId);
}
