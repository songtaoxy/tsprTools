package com.st.modules.collection.list;

import java.util.ArrayList;

/**
 * @author: st
 * @date: 2023/11/23 10:01
 * @version: 1.0
 * @description:
 */
public class ListMain {

	public static void main(String[] args) {
		ArrayList<String> strings = new ArrayList<>();

		strings.add("100");

		String s1 = "100";
		String s2 = "200";

		System.out.println(strings.contains(s1));
		System.out.println(strings.contains(s2));

	}
}
