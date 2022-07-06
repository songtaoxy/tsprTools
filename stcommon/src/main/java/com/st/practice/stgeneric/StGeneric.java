package com.st.practice.stgeneric;

/**
 * @author: st
 * @date: 2022/5/20 10:23
 * @version: 1.0
 * @description:
 */

public class StGeneric {
	public <T extends Number> void show(T t) {
		System.out.println(t);
	}

	public static void main(String[] args) {

	/*	StGeneric generic = new StGeneric();
		generic.show("旭旭宝宝");
		generic.show(20);
		generic.show(true);
		generic.show(99.999);*/
	}
}
