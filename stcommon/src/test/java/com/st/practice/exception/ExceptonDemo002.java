package com.st.practice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 何时需要使用throws
 *
 *
 * @author: st
 * @date: 2021/11/13 14:45
 * @version: 1.0
 * @description:
 */
public class ExceptonDemo002 {

	@Test
	@DisplayName("何时使用throws-没有throw时-无需throws")
	void met_001() {
		System.out.println(1 / 0);
		System.out.println("异常之后的代码能否继续执行");
	}

	@Test
	@DisplayName("何时使用throws-没有throw时-无需throws")
	void met_002() {
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("异常之后的代码能否继续执行");
	}

	@Test
	@DisplayName("何时使用throws-没有throw时-无需throws")
	void met_003() {
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			throw e;
		}

		System.out.println("异常之后的代码能否继续执行");
	}

	@Test
	@DisplayName("何时使用throws-没有throw时-无需throws")
	void met_004() throws Exception {
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			throw new Exception(e);
		}

		System.out.println("异常之后的代码能否继续执行");
	}
}
