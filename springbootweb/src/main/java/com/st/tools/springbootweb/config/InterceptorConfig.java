package com.st.tools.springbootweb.config;

import com.st.tools.springbootweb.interceptor.InterceptorDemo;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

/**
 * @author: st
 * @date: 2021/11/12 15:48
 * @version: 1.0
 * @description:
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    WebMvcConfigurer.super.addInterceptors(registry);
    registry.addInterceptor(new InterceptorDemo()).addPathPatterns("/**"); // 拦截所有请求
  }
}
