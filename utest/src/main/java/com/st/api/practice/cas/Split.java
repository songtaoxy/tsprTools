package com.st.api.practice.cas;

import java.util.Arrays;

/**
 * @author: st
 * @date: 2023/4/10 17:37
 * @version: 1.0
 * @description:
 */
public class Split {

	public static void main(String[] args) {
		String a = "a;b";
		String b = "l1;l2;;l4";
		//String b = "l1;l2;;l4;l5;l6";

		String[] split = b.split(";");
		System.out.println(split[0]);
		System.out.println(split[2]);

		Arrays.stream(split).forEach(System.out::println);
		System.out.println("....");
	}
}
