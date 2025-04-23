package com.st.tools.springbootweb.config;

import com.st.tools.springbootweb.filter.BaseFilter;
import com.st.tools.springbootweb.filter.FilterDemo01;
import com.st.tools.springbootweb.filter.FilterDemo02;
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
		FilterRegistrationBean<FilterDemo01> bean = new FilterRegistrationBean<>();
		bean.setOrder(1);
		bean.setFilter(new FilterDemo01());
		// 匹配"/hello/"下面的所有url
		//bean.addUrlPatterns("/hello/*");
		bean.addUrlPatterns("/*");
		return bean;
	}

	@Bean
	public FilterRegistrationBean registerMyFilter2(){
		FilterRegistrationBean<FilterDemo02> bean = new FilterRegistrationBean<>();
		bean.setOrder(2);
		bean.setFilter(new FilterDemo02());
		// 匹配"/hello/"下面的所有url
		//bean.addUrlPatterns("/hello/*");
		bean.addUrlPatterns("/*");
		return bean;
	}

	@Bean
	public FilterRegistrationBean registerMyFilter3(){
		FilterRegistrationBean<BaseFilter> bean = new FilterRegistrationBean<>();
		bean.setOrder(2);
		bean.setFilter(new BaseFilter());
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
