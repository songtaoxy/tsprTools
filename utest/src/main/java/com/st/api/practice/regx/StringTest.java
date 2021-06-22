package com.st.api.practice.regx;

/**
 * @author: st
 * @date: 2021/5/29 23:58
 * @version: 1.0
 * @description:
 */
public class StringTest {

	public static void main(String[] args) {
		String s1 = "src";

		String s2 = s1;

		s2.replace("c", "xxxx");
		System.out.println(s2);
		s2 = s2.replace("c", "xxxx");
		System.out.println(s2);

	}


}
