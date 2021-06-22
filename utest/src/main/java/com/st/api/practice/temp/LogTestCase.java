package com.st.api.practice.temp;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: st
 * @date: 2021/6/7 01:53
 * @version: 1.0
 * @description:
 */
@Slf4j
public class LogTestCase {
	public static void main(String[] args) {
		log.info("god");


		//log.info(System.getProperty("java.class.path"));
		new LogTestCase().m1();

	}

	public void m1(){
		log.info("hi,hello world");
	}

}
