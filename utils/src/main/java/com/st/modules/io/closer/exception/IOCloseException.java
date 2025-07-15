package com.st.modules.io.closer.exception;

import java.io.IOException;

public class IOCloseException extends RuntimeException {
    public IOCloseException(String message, IOException cause) {
        super(message, cause);
    }
}
