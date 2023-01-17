package com.st.practice.testable2;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.google.errorprone.annotations.Var;
import com.st.practice.testable.Demo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class D1Test {

	D1 d1 = new D1();
	public static class Mock {


		@MockInvoke(targetClass = D1.class)
		String m1() {
			return "sub2..................>";
		}
	}


	@Test
	void m2(){
		String s = d1.m2();
		System.out.println(s);
	}
}