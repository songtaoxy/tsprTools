package com.st.tools.springbootweb.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.tools.springbootweb.utils.log.NoLogParams;
import com.st.tools.springbootweb.utils.mask.SensitiveFieldMasker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
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

    @Pointcut("execution(public * com.st.tools..*.controller..*.*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        Method method = methodSig.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

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
            log.info("➡️ {}.{} 请求参数: [{}]", className, methodName, argsStr);
        }

        Object result;
        try {
            result = joinPoint.proceed();

            String resultStr = objectMapper.writeValueAsString(result);
            resultStr = masker.maskSensitiveFields(resultStr);
            log.info("⬅️ {}.{} 响应结果: {}", className, methodName, resultStr);

            return result;
        } catch (Throwable e) {
            log.error("❌ {}.{} 调用异常: {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }


    @PostConstruct
    public void init() {
        log.info("SensitiveFieldMasker: {}, ObjectMapper: {}", masker, objectMapper);
    }
}
