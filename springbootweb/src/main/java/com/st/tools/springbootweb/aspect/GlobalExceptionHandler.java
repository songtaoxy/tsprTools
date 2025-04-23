package com.st.tools.springbootweb.aspect;

import com.st.tools.springbootweb.exception.BizException;
import com.st.tools.springbootweb.i18n.I18nUtil;
import com.st.tools.springbootweb.response.StatCode;
import com.st.tools.springbootweb.response.Result;
import com.st.tools.springbootweb.response.Response;
import com.st.tools.springbootweb.utils.bean.SpringContextUtils;
import com.st.tools.springbootweb.utils.trace.TraceIdContext;
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
    public Response<Result> handleBizException(BizException ex, HttpServletRequest request, Locale locale) {

        StatCode errorCode = StatCode.BIZ_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);

        return buildErrorResponse(String.valueOf(ex.getCode()), value, ex.getDetail(), request.getRequestURI());
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<Result> handleNullPointer(NullPointerException ex, HttpServletRequest request, Locale locale) {

        StatCode errorCode = StatCode.NULL_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);

        return buildErrorResponse(code, value, ex.getMessage(), request.getRequestURI());
    }


    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Response<Result> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex, HttpServletRequest request, Locale locale) {
        String fieldMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst().orElse("参数校验失败");

        StatCode errorCode = StatCode.VALIDATION_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);

        return buildErrorResponse(code, value, fieldMessage, request.getRequestURI());
    }


    /**
     * <li>国际化配置: ref springbootweb/src/main/resources/i18n/message.properties</li>
     * <li>兜底异常处理</li>
     */
    @ExceptionHandler(Exception.class)
    public Response<Result> handleException(Exception ex, HttpServletRequest request, Locale locale) {

        StatCode errorCode = StatCode.SYSTEM_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);

        return buildErrorResponse(code, value, ex.getMessage(), request.getRequestURI());
    }

    // local是从哪里获取的?
    private Response<Result> buildErrorResponse(String code, String messageValue, String detail, String path) {

        I18nUtil bean = SpringContextUtils.getBean(I18nUtil.class);
        Locale currentLocale = bean.getCurrentLocale();

        Result result = Result.builder()
                .timestamp(LocalDateTime.now())
                .detail(detail)
                .uri(path)
                .traceId(TraceIdContext.getTraceId())
                .locale(currentLocale.toString())
                .build();
        return Response.custum(code, messageValue, result);
    }
}
