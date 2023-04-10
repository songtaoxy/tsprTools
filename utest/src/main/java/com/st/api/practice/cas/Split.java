package com.st.api.practice.cas;

/**
 * @author: st
 * @date: 2023/4/10 17:37
 * @version: 1.0
 * @description:
 */
public class Split {

	public static void main(String[] args) {
		String a = "a;b";
		String b = ";b";

		String[] split = b.split(";");
		System.out.println(split[0]);
		System.out.println(split[1]);
	}
}
