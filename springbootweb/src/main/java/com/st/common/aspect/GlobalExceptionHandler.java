package com.st.common.aspect;

import com.st.common.exception.BizException;
import com.st.common.exception.ExceptionUtils;
import com.st.common.i18n.I18nUtil;
import com.st.common.response.StatCode;
import com.st.common.response.Result;
import com.st.common.response.Response;
import com.st.common.utils.bean.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

        return buildErrorResponse(String.valueOf(ex.getCode()), value, ExceptionUtils.getCause(ex));
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<Result> handleNullPointer(NullPointerException ex, HttpServletRequest request, Locale locale) {

        StatCode errorCode = StatCode.NULL_ERROR;
        String code = errorCode.getCode();
        String key = errorCode.getI18nKey();
        String value = messageSource.getMessage(key, null, locale);


        return buildErrorResponse(code, value, ExceptionUtils.getCause(ex));
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

        return buildErrorResponse(code, value, fieldMessage);
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

        return buildErrorResponse(code, value, ExceptionUtils.getCause(ex));
    }

    // local是从哪里获取的?
    private Response<Result> buildErrorResponse(String code, String messageValue, String detail) {

        I18nUtil bean = SpringContextUtils.getBean(I18nUtil.class);
        Locale currentLocale = bean.getCurrentLocale();

       /* Result result = Result.builder()
                .timestamp(LocalDateTime.now())
                .detail(detail)
                .uri(path)
                .traceId(TraceIdContext.getTraceId())
                .locale(currentLocale.toString())
                .build();*/

        Result result = Result.build(detail);
        return Response.custum(code, messageValue, result);
    }
}
