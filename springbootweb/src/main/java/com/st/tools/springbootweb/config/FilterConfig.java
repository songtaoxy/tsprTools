package com.st.tools.springbootweb.config;

import com.st.tools.springbootweb.filter.FilterDemo;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: st
 * @date: 2021/11/12 15:07
 * @version: 1.0
 * @description:
 */
@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean registerMyFilter(){
		FilterRegistrationBean<FilterDemo> bean = new FilterRegistrationBean<>();
		bean.setOrder(1);
		bean.setFilter(new FilterDemo());
		// 匹配"/hello/"下面的所有url
		//bean.addUrlPatterns("/hello/*");
		bean.addUrlPatterns("/*");
		return bean;
	}
	/*@Bean
	public FilterRegistrationBean registerMyAnotherFilter(){
		FilterRegistrationBean<MyAnotherFilter> bean = new FilterRegistrationBean<>();
		bean.setOrder(2);
		bean.setFilter(new MyAnotherFilter());
		// 匹配所有url
		bean.addUrlPatterns("/*");
		return bean;
	}*/
}
