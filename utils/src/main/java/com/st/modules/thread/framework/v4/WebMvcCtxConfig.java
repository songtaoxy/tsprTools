package com.st.modules.thread.framework.v4;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcCtxConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<BizContextFilter> bizContextFilter(){
        FilterRegistrationBean<BizContextFilter> r = new FilterRegistrationBean<BizContextFilter>();
        r.setFilter(new BizContextFilter());
        r.setOrder(1);
        r.addUrlPatterns("/*");
        return r;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new BizContextArgumentResolver());
    }
}
