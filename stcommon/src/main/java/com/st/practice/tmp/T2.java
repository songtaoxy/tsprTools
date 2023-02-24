package com.st.practice.tmp;

import cn.hutool.core.util.ObjectUtil;

/**
 * @author: st
 * @date: 2023/2/6 18:59
 * @version: 1.0
 * @description:
 */
public class T2 {


	public static void main(String[] args) {
		Integer x = 14;
		String y = "14";

		boolean equals = ObjectUtil.equals(x, y);

		System.out.println(equals);


		int[] ints = new int[1];
		ints[0]=0;
		//ints[1]=1;
		//ints[2]=2;
		for (int i : ints) {
			System.out.println(i);
		}
	}
}
