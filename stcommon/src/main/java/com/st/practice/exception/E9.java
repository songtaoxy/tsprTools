package com.st.practice.exception;

public class E9 {
	public static void main(String[] args) {
		System.out.println("main");
		try {
			m_a();
		} catch (Exception e) {
			System.out.println("==================");
			System.out.println("main  message: " + e.getMessage());
			System.out.println("main  e: " + e);
			System.out.println("main  cause: " + e.getCause());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		System.out.println("main_2");

	}

	public static void m_a() {
		System.out.println("m_a");
		try {
			m_b();
		} catch (Exception e) {
			System.out.println("==================");
			System.out.println("ma message: " + e.getMessage());
			System.out.println("ma e: " + e);
			System.out.println("ma cause: " + e.getCause());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		System.out.println("m_a_2");
	}

	public static void m_b() throws Exception {
		System.out.println("m_b");
		try {
			int i = 1 / 0;
			//throw new Exception("hi");
		} catch (Exception e) {
			System.out.println("==================");
			System.out.println("mb message: " + e.getMessage());
			System.out.println("mb e: " + e);
			System.out.println("mb cause: " + e.getCause());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		System.out.println("m_b_2");
	}
}
