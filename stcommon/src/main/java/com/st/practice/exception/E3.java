package com.st.practice.exception;
public class E3 {
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
		try {
			int i = 1 / 0;
		} catch (Exception e) {
			System.out.println("message: "+e.getMessage());
			System.out.println("e: "+e);
			System.out.println("cause: "+e.getCause());
			e.printStackTrace();
		}
		System.out.println("m_b_2");
	}
}
