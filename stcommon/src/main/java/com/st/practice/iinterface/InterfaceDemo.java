package com.st.practice.iinterface;

/**
 * @author: st
 * @date: 2022/5/18 14:54
 * @version: 1.0
 * @description:
 */
public class InterfaceDemo  {

	public static int num = 10000;
	public static String num2;
	//public static final String num3 = "num3";
	public final String num4 = "num4";




	public static void main(String args[]) {

		InterfaceDemo obj = new InterfaceDemo();
		System.out.println("Value: " + obj.num);

		num2 = "hi";
		num2 = "hi...";

		//num3 = "num3 ... appendd";

	}
}

