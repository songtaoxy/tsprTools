package test;

/**
 * @author: st
 * @date: 2023/3/10 10:00
 * @version: 1.0
 * @description:
 */

import java.util.concurrent.Semaphore;

public class Order_aa_bb_cc {

	public static void main(String[] args) {
		// 初始化许可数为1，A线程可以先执行
		Semaphore semaphoreA = new Semaphore(1);
		// 初始化许可数为0，B线程阻塞
		Semaphore semaphoreB = new Semaphore(0);
		// 初始化许可数为0，C线程阻塞
		Semaphore semaphoreC = new Semaphore(0);

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				try {
					// A线程获得许可，同时semaphoreA的许可数减为0,进入下一次循环时
					// A线程会阻塞，知道其他线程执行semaphoreA.release();
					semaphoreA.acquire();
					for(int j=0;j<5;j++) {
						// 打印当前线程名称
						System.out.print(Thread.currentThread().getName());
					}
					// semaphoreB许可数加1

					semaphoreB.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "A").start();

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				try {
					semaphoreB.acquire();
					for(int j=0;j<10;j++) {
						System.out.print(Thread.currentThread().getName());
					}
					semaphoreC.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "B").start();

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				try {
					semaphoreC.acquire();
					for(int j=0;j<15;j++) {
						System.out.print(Thread.currentThread().getName());
					}
					System.out.println();
					semaphoreA.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "C").start();
	}

}
