package com.st.modules.thread.framework.v3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryTicketStore implements TicketStore {
    private final ConcurrentMap<String,Object> map = new ConcurrentHashMap<String,Object>();
    public void save(String taskId, Object value) { map.put(taskId, value); }
    public Object get(String taskId) { return map.get(taskId); }
}
