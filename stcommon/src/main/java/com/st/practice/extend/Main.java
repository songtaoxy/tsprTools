package com.st.practice.extend;

/**
 * @author: st
 * @date: 2022/4/20 17:23
 * @version: 1.0
 * @description:
 */
public class Main {

	public static void main(String[] args) {
		Son son = new Son("son");
		System.out.println(son.getName());


		Son2 son2 = new Son2("son2");
		System.out.println(son2.getName());
		son2.setName("son2-2");
		System.out.println(son2.getName());

	}
}
