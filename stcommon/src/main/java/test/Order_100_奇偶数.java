package test;

import java.util.concurrent.Semaphore;

/**
 * @author: st
 * @date: 2023/3/10 10:12
 * @version: 1.0
 * @description:
 */
public class Order_100_奇偶数 {

	public static void main(String[] args) {
		// 初始化许可数为1，A线程可以先执行
		Semaphore semaphoreA = new Semaphore(1);
		// 初始化许可数为0，B线程阻塞
		Semaphore semaphoreB = new Semaphore(0);
		// 初始化许可数为0，C线程阻塞

		new Thread(() -> {
			for (int i = 1; i <= 100; i++) {
				try {
					// A线程获得许可，同时semaphoreA的许可数减为0,进入下一次循环时
					// A线程会阻塞，知道其他线程执行semaphoreA.release();
					semaphoreA.acquire();
					// 打印当前线程名称
					//System.out.print(Thread.currentThread().getName());
					if (!(i % 2 == 0)) {
						System.out.println("基数 -> ["+i+"]");
					}
					// semaphoreB许可数加1
					semaphoreB.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "A").start();

		new Thread(() -> {
			for (int i = 1; i <= 100; i++) {
				try {
					semaphoreB.acquire();
					//System.out.print(Thread.currentThread().getName());
					if (i % 2 == 0) {
						//System.out.print((char)('A'+i));
						System.out.println("偶数 -> ["+i+"]");
					}
					semaphoreA.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "B").start();


	}


}
