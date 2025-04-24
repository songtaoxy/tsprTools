package com.st.tools.springbootweb.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.modules.json.jackson.JacksonUtils;
import com.st.tools.springbootweb.response.Response;
import com.st.tools.springbootweb.response.Result;
import com.st.tools.springbootweb.utils.log.NoLogParams;
import com.st.tools.springbootweb.utils.mask.SensitiveFieldMasker;
import jdk.internal.org.objectweb.asm.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerLogAspect {

    /**
     * <li>@RequiredArgsConstructor会为以下两个属性注入
     * <ul>@Component：将 ControllerLogAspect 注册为一个 Spring Bean</ul>
     * <ul>@RequiredArgsConstructor：Lombok 自动为 final 字段生成构造函数</ul>
     * <ul>Spring 在启动时会自动调用这个构造函数并注入相应的 Bean</ul>
     * <ul>如何验证: ref {@link ControllerLogAspect#init() }</ul>
     * </li>
     */
    private final SensitiveFieldMasker masker;
    private final ObjectMapper objectMapper;

    private static final String START_TIME = "start-time";


    @Pointcut("execution(public * com.st.tools..*.controller..*.*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        request.setAttribute(START_TIME, System.currentTimeMillis());

        String doubleLine ="\n================================================================================== \n";

        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        Method method = methodSig.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 获取请求路径、请求方、Locale
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Locale locale = request.getLocale();
        String path = request.getRequestURI();

        // 示例：根据当前方法名作为国际化 key（更正式可以写注解控制）
        /*String i18nKey = "log.api." + methodName;
        String i18nMessage = i18nUtil.getMessage(i18nKey);*/


        boolean skipLogParams = method.isAnnotationPresent(NoLogParams.class);

        // 请求参数打印
        if (!skipLogParams) {
            Object[] args = joinPoint.getArgs();
            String argsStr = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse))
                    .map(arg -> {
                        try {
                            String raw = objectMapper.writeValueAsString(arg);
                            return masker.maskSensitiveFields(raw);
                        } catch (Exception e) {
                            return String.valueOf(arg);
                        }
                    })
                    .collect(Collectors.joining(", "));
//            log.info("➡️path:{},  {}.{} 请求参数: [{}], 来自客户端区域: {}",path, className, methodName, argsStr, locale);

            log.info("{}➡️ {}, {}#{} 请求参数如下: {} [{}]", doubleLine,path, className, methodName,doubleLine, JacksonUtils.toPrettyJson(Result.build(argsStr)));

//            log.info("\n=================================== \n 请求信息,如下: \n===================================\n");
//            log.info(JacksonUtils.toPrettyJson(Result.build(argsStr)));
        }

        Object result;
        try {
            result = joinPoint.proceed();

            String resultStr = objectMapper.writeValueAsString(result);
            resultStr = masker.maskSensitiveFields(resultStr);

//            log.info("\n=================================== \n 返回信息,如下: \n===================================\n");
//            log.info("\n⬅️ {}.{} 响应结果: {}", className, methodName, JacksonUtils.toPrettyJson(JacksonUtils.fromJson(resultStr,Response.class)));
//            Long start = (Long) request.getAttribute(START_TIME);

            String start_time = MDC.get("START_TIME");
            long duration = System.currentTimeMillis() - Long.parseLong(start_time);

            log.info("{}⬅️ {}, {}#{}, 耗时:{}ms, 返回信息如下: {} [{}]", doubleLine,path, className, methodName,String.valueOf(duration),doubleLine, JacksonUtils.toPrettyJson(JacksonUtils.fromJson(resultStr,Response.class)));

            return result;
        } catch (Throwable e) {
            // 如果返回, 有异常, 只打印一次异常堆栈.避免多次打印
//            log.error("❌ {}.{} 返回时, 调用异常: {}", className, methodName, e.getMessage(), e);

            String start_time = MDC.get("START_TIME");
            long duration = System.currentTimeMillis() - Long.parseLong(start_time);

//            String msgs = doubleLine + "⬅️❌, " + path +", "+className + "#"+methodName+", 耗时" +duration +"ms, 返回时调用异常:"+e.getMessage();
            log.error("{}⬅️❌ {}, {}#{}, 耗时:{}ms, 返回时调用异常: {} {}", doubleLine,path, className, methodName,String.valueOf(duration), e.getMessage(),doubleLine, e);
//            log.error(msgs,e);
            throw e;
        }
    }


    @PostConstruct
    public void init() {
        log.info("SensitiveFieldMasker: {}, ObjectMapper: {}", masker, objectMapper);
    }
}
