package com.st.modules.demo.service.impl;

import com.st.modules.demo.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * @author: st
 * @date: 2021/6/5 02:58
 * @version: 1.0
 * @description:
 */
@Service
public class HelloImpl implements HelloService {
	@Override
	public String sayHello() {
		return "hello";
	}
}
