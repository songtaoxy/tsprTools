package com.st.tools.springbootweb.exception;

import org.springframework.http.HttpStatus;

public class BusinessException  extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
