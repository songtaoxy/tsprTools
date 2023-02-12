package com.st.practice.exception;

public class E1 {
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

	public static void m_b() {
		System.out.println("m_b");
		System.out.println("m_b_2");
	}
}
