package com.st.infrastructure.client.http.webclient;

public enum ApiErrorCode {
    OK,
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    UPSTREAM_TIMEOUT,
    UPSTREAM_ERROR,
    INTERNAL_ERROR,
    RATE_LIMITED,
    DEPENDENCY_ERROR
}
