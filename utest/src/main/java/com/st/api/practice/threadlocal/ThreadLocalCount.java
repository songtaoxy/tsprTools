package com.st.api.practice.threadlocal;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: st
 * @date: 2021/6/11 21:51
 * @version: 1.0
 * @description:
 */

/**
 * 一个 ThreadLocal 对象只能存一个变量. 如果后面继续往里面set, 会覆盖前面的.
 * 一个Thread里面可以绑定多个 ThreadLocal 对象,每个 ThreadLocal 对象只能存一个变量
 */
@Slf4j
public class ThreadLocalCount {

	public static void main(String[] args) {

		ThreadLocal<String> stringThreadLocal_1 = new ThreadLocal<>();
		stringThreadLocal_1.set("s1");
		stringThreadLocal_1.set("s2");
		log.info("[当前线程: {}] ===> {}",Thread.currentThread().getName(),stringThreadLocal_1.get());


		ThreadLocal<String> stringThreadLocal_2 = new ThreadLocal<>();
		stringThreadLocal_2.set("s3");
		stringThreadLocal_2.set("s4");
		log.info("[当前线程: {}] ===> {}",Thread.currentThread().getName(),stringThreadLocal_2.get());


	}
}
