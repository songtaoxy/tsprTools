package com.st.infrastructure.client.http.webclient;

public class AiServiceException extends RuntimeException {
    private final String code;
    private final String traceId;
    public AiServiceException(String message, String code, String traceId, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.traceId = traceId;
    }
    public String getCode() {
        return code;
    }
    public String getTraceId() {
        return traceId;
    }
}
