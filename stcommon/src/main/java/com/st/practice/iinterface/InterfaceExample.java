package com.st.practice.iinterface;

/**
 * @author: st
 * @date: 2022/5/18 14:54
 * @version: 1.0
 * @description:
 */
public class InterfaceExample implements MyInterface {

	public static int num = 10000;
	public static String num2;
	public static final String num3 = "num3" ;
	//public final String num4 = null;



	@Override
	public void display() {

		System.out.println("This is the implementation of the display method");

	}

	public void show() {

		System.out.println("This is the implementation of the show method");

	}

	public static void main(String args[]) {

		InterfaceExample obj = new InterfaceExample();

		System.out.println("Value of num of the interface " + MyInterface.num);

		System.out.println("Value of num of the class " + obj.num);

		num2 = "hi";
		num2 = "hi...";

		//num3 = "num3 ... appendd";

	}
}

