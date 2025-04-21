package com.st.tools.springbootweb.exception;

public class BizException extends RuntimeException {
    private final int code;
    private final String detail;

    public BizException(int code, String message, String detail) {
        super(message);
        this.code = code;
        this.detail = detail;
    }

    public BizException(int code, String message) {
        this(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }
}
