package com.st.tools.springbootweb.aspect;

import com.st.tools.springbootweb.exception.BizException;
import com.st.tools.springbootweb.response.ErrorResult;
import com.st.tools.springbootweb.response.Response;
import com.st.tools.springbootweb.trace.TraceIdContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BizException.class)
    public Response<ErrorResult> handleBizException(BizException ex, HttpServletRequest request, Locale locale) {
        return buildErrorResponse(String.valueOf(ex.getCode()), "error.biz", ex.getDetail(), request.getRequestURI(), locale);
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<ErrorResult> handleNullPointer(NullPointerException ex, HttpServletRequest request, Locale locale) {
        return buildErrorResponse("400", "error.null", ex.getMessage(), request.getRequestURI(), locale);
    }


    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Response<ErrorResult> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex, HttpServletRequest request, Locale locale) {
        String fieldMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst().orElse("参数校验失败");
        return buildErrorResponse("400", "error.validation", fieldMessage, request.getRequestURI(), locale);
    }


    @ExceptionHandler(Exception.class)
    public Response<ErrorResult> handleException(Exception ex, HttpServletRequest request, Locale locale) {
        return buildErrorResponse("500", "error.internal", ex.getMessage(), request.getRequestURI(), locale);
    }

    private Response<ErrorResult> buildErrorResponse(String code, String messageKey, String detail, String path, Locale locale) {
        String message = messageSource.getMessage(messageKey, null, locale);
        ErrorResult result = ErrorResult.builder()
                .timestamp(LocalDateTime.now())
                .detail(detail)
                .path(path)
                .traceId(TraceIdContext.getTraceId())
                .build();
        return Response.fail(code, message, result);
    }
}
