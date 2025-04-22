package com.st.tools.springbootweb.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SYSTEM_ERROR("500", "error.internal"),
    VALIDATION_ERROR("400", "error.validation"),
    BIZ_ERROR("400","error.biz"),
    NULL_ERROR("410","error.null"),
    NOT_FOUND("404", "error.notfound");



    private final String code;
    private final String i18nKey;
}