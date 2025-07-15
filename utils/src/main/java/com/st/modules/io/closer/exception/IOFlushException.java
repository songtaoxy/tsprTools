package com.st.modules.io.closer.exception;

import java.io.IOException;

public class IOFlushException extends RuntimeException {
    public IOFlushException(String message, IOException cause) {
        super(message, cause);
    }
}

