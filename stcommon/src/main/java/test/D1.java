package test;

import java.util.Scanner;

/**
 * @author: st
 * @date: 2023/3/10 09:30
 * @version: 1.0
 * @description:
 */
public class D1 {

	public static int factorial(int n){
		if(n == 1){
			return 1;
		}
		else{
			return n*factorial(n-1);
		}
	}

	//主方法
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);//声明Scanner对象
		System.out.print("请输入数字:"); //提示用户输入数字
		int num = scanner.nextInt();//定义num接受输入的数字
		int ret = factorial(num);//调用阶乘方法
		System.out.println(num+"的阶乘为"+ret);//输出返回值
	}
}
