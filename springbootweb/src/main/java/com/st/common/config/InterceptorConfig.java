package com.st.common.config;

import com.st.common.interceptor.InterceptorDemo;
import com.st.common.interceptor.RequestLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: st
 * @date: 2021/11/12 15:48
 * @version: 1.0
 * @description:
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

  @Autowired
  private RequestLogInterceptor requestLogInterceptor;

  public InterceptorConfig(RequestLogInterceptor requestLogInterceptor) {
    this.requestLogInterceptor = requestLogInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    WebMvcConfigurer.super.addInterceptors(registry);
    registry.addInterceptor(new InterceptorDemo()).addPathPatterns("/**"); // 拦截所有请求

    registry.addInterceptor(requestLogInterceptor).addPathPatterns("/**");
  }
}
