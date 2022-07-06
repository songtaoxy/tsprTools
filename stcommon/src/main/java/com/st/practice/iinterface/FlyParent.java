package com.st.practice.iinterface;

/**
 * @author: st
 * @date: 2022/5/18 10:01
 * @version: 1.0
 * @description:
 */
public class FlyParent {


	Integer integer = 100;
	int anInt = 10;

	public void fly() {
		System.out.println("parent fly ....");

	}


	public void eat() {
		System.out.println("parent eating ....");
	}

	public final void  final_method() {
		System.out.println("final method");
	}
}
