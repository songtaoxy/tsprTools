package com.st.tools.common.config;

import com.st.tools.common.filter.BaseFilter;
import com.st.tools.common.filter.FilterDemo01;
import com.st.tools.common.filter.FilterDemo02;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author: st
 * @date: 2021/11/12 15:07
 * @version: 1.0
 * @description:
 */
@Configuration
public class FilterConfig {


	@Bean
	public FilterRegistrationBean<Filter> registerMyFilter3(){
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
		bean.setOrder(1);
		bean.setFilter(new BaseFilter());
		// 匹配"/hello/"下面的所有url
		//bean.addUrlPatterns("/hello/*");
		bean.addUrlPatterns("/*");
		return bean;
	}


	@Bean
	public FilterRegistrationBean<Filter> registerMyFilter(){
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new FilterDemo01());
		//order 越小越先执行
		bean.setOrder(2);
		// 匹配"/hello/"下面的所有url
		//bean.addUrlPatterns("/hello/*");
		bean.addUrlPatterns("/*");
		return bean;
	}

	@Bean
	public FilterRegistrationBean<Filter> registerMyFilter2(){
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
		bean.setOrder(3);
		bean.setFilter(new FilterDemo02());
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
