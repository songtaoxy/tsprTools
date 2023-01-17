package com.st.practice.testable2;

/**
 * @author: st
 * @date: 2023/1/18 01:49
 * @version: 1.0
 * @description:
 */
public class D1 {

	String m1() {
		return "m1";
	}

	String m2(){

		return m1();
	}
}
