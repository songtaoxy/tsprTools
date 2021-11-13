package com.st.practice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 如何查看异常日志
 *
 * <ul>
 *     <li>
 *         一个调用链中, 一个异常, 会有多少个异常日志
 *     </li>
 *     <li>
 *         各类日志格式是怎样的
 *     </li>
 *     <li>
 *         为什么有的时候,会有caused by; 而有时没有
 *     </li>
 * </ul>
 *
 * @author: st
 * @date: 2021/11/13 15:14
 * @version: 1.0
 * @description:
 */
public class ExceptionCase004 {

	@Test
	@DisplayName("一个异常会有多少个异常日志")
	void met_001() {
		System.out.println(1 / 0);
		System.out.println("异常之后的代码能否继续执行");
	}

	@Test
	@DisplayName("一个异常会有多少个异常日志")
	void met_002() {
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("异常之后的代码能否继续执行");
	}

	@Test
	@DisplayName("一个异常会有多少个异常日志")
	void met_003() {
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			throw e;
		}

		System.out.println("异常之后的代码能否继续执行");
	}

	@Test
	@DisplayName("一个异常会有多少个异常日志")
	void met_004() throws Exception {
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			throw new Exception(e);
		}

		System.out.println("异常之后的代码能否继续执行");
	}

	@Test
	@DisplayName("一个异常会有多少个异常日志")
	void case005() throws Exception {
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			throw new Exception("被除数不能是0 ===> "+e.getMessage(),e);
		}

		System.out.println("异常之后的代码能否继续执行");
	}
}
