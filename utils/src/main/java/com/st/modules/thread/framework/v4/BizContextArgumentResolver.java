package com.st.modules.thread.framework.v4;


import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.*;

public final class BizContextArgumentResolver implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter p){
        return p.hasParameterAnnotation(CurrentBizContext.class) || p.getParameterType()==BizContext.class;
    }
    public Object resolveArgument(MethodParameter p, ModelAndViewContainer m, NativeWebRequest w, WebDataBinderFactory f) {
        return BizContextHolder.get();
    }
}

