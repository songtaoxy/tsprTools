package com.st.dsa.offer;

/**
 * @author: st
 * @date: 2023/2/12 23:13
 * @version: 1.0
 * @description:
 */
public class 动态规划_青蛙跳 {

	public static void main(String[] args) {
		int i = new 动态规划_青蛙跳().numWays(10);
		System.out.println(i);

		System.out.println(9/4);
		System.out.println(9%4);
		System.out.println(4%4);
		System.out.println(3%4);
		System.out.println(2%4);
		System.out.println(1%4);
	}

	public int numWays(int n) {
		if (n<= 1) {
			return 1;
		}
		if (n == 2) {
			return 2;
		}
		int a = 1;
		int b = 2;
		int temp = 0;
		for (int i = 3; i <= n; i++) {
			//temp = (a + b)% 1000000007;
			temp = (a + b)% 1000000007;
			a = b;
			b = temp;
		}
		return temp;
	}
}
