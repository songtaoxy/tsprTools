package com.st.tools.springbootweb.aspect;

import com.st.tools.springbootweb.exception.BizException;
import com.st.tools.springbootweb.response.ErrorCode;
import com.st.tools.springbootweb.response.ErrorResult;
import com.st.tools.springbootweb.response.Response;
import com.st.tools.springbootweb.trace.TraceIdContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @Autowired
    private final MessageSource messageSource;

    @ExceptionHandler(BizException.class)
    public Response<ErrorResult> handleBizException(BizException ex, HttpServletRequest request, Locale locale) {

        ErrorCode errorCode = ErrorCode.BIZ_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);

        return buildErrorResponse(String.valueOf(ex.getCode()), value, ex.getDetail(), request.getRequestURI(), locale);
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<ErrorResult> handleNullPointer(NullPointerException ex, HttpServletRequest request, Locale locale) {

        ErrorCode errorCode = ErrorCode.NULL_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);

        return buildErrorResponse(code, value, ex.getMessage(), request.getRequestURI(), locale);
    }


    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Response<ErrorResult> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex, HttpServletRequest request, Locale locale) {
        String fieldMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst().orElse("参数校验失败");

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);

        return buildErrorResponse(code, value, fieldMessage, request.getRequestURI(), locale);
    }


    @ExceptionHandler(Exception.class)
    public Response<ErrorResult> handleException(Exception ex, HttpServletRequest request, Locale locale) {

        ErrorCode errorCode = ErrorCode.SYSTEM_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);

        return buildErrorResponse(code, value, ex.getMessage(), request.getRequestURI(), locale);
    }

    // local是从哪里获取的?
    private Response<ErrorResult> buildErrorResponse(String code, String messageValue, String detail, String path, Locale locale) {
//        String message = messageSource.getMessage(messageKey, null, locale);
        ErrorResult result = ErrorResult.builder()
                .timestamp(LocalDateTime.now())
                .detail(detail)
                .path(path)
                .traceId(TraceIdContext.getTraceId())
                .build();
        return Response.fail(code, messageValue, result);
    }
}
