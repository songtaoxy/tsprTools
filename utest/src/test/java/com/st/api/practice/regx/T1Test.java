package com.st.api.practice.regx;

import com.alibaba.testable.core.annotation.MockInvoke;
import org.junit.jupiter.api.Test;

class T1Test {

	T1 t1 = new T1();

	public static class Mock {
		// 放置Mock方法的地方
		@MockInvoke(targetClass = T1.class)
		public String pullconfigParser(String strSrc, String poleId) {
			return "......";
		}
	}


	@Test
	void callPull() {

		String s1 = "we";
		String s2 = "12.3";

		String s = t1.callPull(s1, s2);
		System.out.println(s);
	}
}