package com.st.practice.exception;

/**
 * @author: st
 * @date: 2023/2/8 10:25
 * @version: 1.0
 * @description:
 */
public class E13_编译器异常 {

	public static void main(String[] args) {
		System.out.println("main");
		m_a();
		System.out.println("main_2");

	}

	public static void m_a() {
		System.out.println("m_a");
		m_b();
		System.out.println("m_a_2");
	}

	public static void m_b()  {
		System.out.println("m_b");
		System.out.println("m_b_2");
		//throw new Exception("");
	}

}
