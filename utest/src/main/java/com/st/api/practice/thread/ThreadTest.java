package com.st.api.practice.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: st
 * @date: 2021/5/30 02:40
 * @version: 1.0
 * @description:
 */
public class ThreadTest {
	public static void main(String[] args) {
		//Executors: 创建线程池的工具类, 可以创建不同类型的线程池
		//ExecutorService: 线程池, 是个接口, 面向接口编程
		//ThreadPoolExecutor: 线程池, 具体的事实现类
		//ExecutorService executorService = Executors.newCachedThreadPool();
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;

		// 设置线程池的相关参数
		//threadPoolExecutor.setMaximumPoolSize(5);
		//threadPoolExecutor.setCorePoolSize(5);

		// 向线程池提交任务, 执行
		threadPoolExecutor.execute(new NumbreThread());
		threadPoolExecutor.execute(new NumbreThread());
		threadPoolExecutor.execute(new NumbreThread());

		threadPoolExecutor.execute(new NumbreThread2());
		threadPoolExecutor.execute(new NumbreThread2());
		threadPoolExecutor.execute(new NumbreThread2());
	}
}

class NumbreThread implements Runnable{

	@Override
	public void run() {
		for (int i = 0; i <3 ; i++) {
			if (i % 2 == 0) {
				System.out.println(Thread.currentThread().getName()+":"+i);
			}
		}
	}
}

class NumbreThread2 implements Runnable{


	@Override
	public void run() {

		for (int i = 0; i < 3;i++) {
			if (i % 2 == 0) {
				System.out.println(Thread.currentThread().getName()+"===>"+i);
			}
		}
	}
}