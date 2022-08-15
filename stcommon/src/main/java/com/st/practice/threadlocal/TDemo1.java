package com.st.practice.threadlocal;

/**
 * @author: st
 * @date: 2022/8/11 00:53
 * @version: 1.0
 * @description:
 */
public class TDemo1 {
	private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>() {
		// 复写initialValue方法为ThreadLocal设置一个初始值，并获取调用了threadLocal的线程id
		@Override
		protected Integer initialValue() {
			System.out.println("当前的线程id：" + Thread.currentThread().getId());
			return 10;
		}
	};

	public static void main(String[] args) {
		// main方法就对应一个线程了，我们在主线程中对threadLocal的值进行修改
		System.out.println("～～～～～～～～～～～～主线程～～～～～～～～～～～～～");
		System.out.println("主线程中, 线程id:" + Thread.currentThread().getId());
		System.out.println("在主线程中获取threadLocal的值：" + threadLocal.get());
		threadLocal.set(100); // 改变threadLocal的值
		System.out.println("在主线程中再次获取threadLocal的值：" + threadLocal.get());

		System.out.println("～～～～～～～～～～～～新线程～～～～～～～～～～～～～");
		// 新创一个线程，并获取threadLocal的值
		new Thread(() ->
		{
			System.out.println("新线程中, 线程id:" + Thread.currentThread().getId());
			System.out.println("在新的线程中获取threadLocal的值：" + threadLocal.get());
		}
		).start();
	}
}
