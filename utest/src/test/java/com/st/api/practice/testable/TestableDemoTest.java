package com.st.api.practice.testable;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.google.errorprone.annotations.Var;
import com.st.api.practice.regx.T1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestableDemoTest {




	public static class Mock {
		// 放置Mock方法的地方
		//
		@MockInvoke(targetClass = TestableDemo.class)
		String m1() {
			return "mock m1";
		}

		@MockInvoke(targetClass = String.class)
		private String substring(int i) {
			return "sub_string";
		}


	}

	@Test
	void m2() {
		TestableDemo testableDemo = new TestableDemo();
		String s = testableDemo.m2();
		System.out.println(s);
	}

	@Test
	void m3() {
		TestableDemo testableDemo = new TestableDemo();
		String s = testableDemo.m3();
		System.out.println(s);

	}
}