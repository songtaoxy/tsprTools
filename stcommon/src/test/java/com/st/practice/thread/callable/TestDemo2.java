package com.st.practice.thread.callable;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @author: st
 * @date: 2023/3/1 16:13
 * @version: 1.0
 * @description:
 */
public class TestDemo2 {

	private final static int TIMES = 60;

	public static void main(String[] args) throws Exception {

		Callable<Integer> cal1 = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Thread.sleep(10000);
				return 11;
			}
		};

		Callable<Integer> cal2 = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 22;
			}
		};

		Callable<Integer> cal3 = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Thread.sleep(7000);
				return 33;
			}
		};

		Callable<Integer> cal4 = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 44;
			}
		};

		Callable<Integer> cal5 = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 55;
			}
		};

		ExecutorService es = Executors.newFixedThreadPool(5);

		ExecutorCompletionService<Integer> service = new ExecutorCompletionService<>(es);

		ArrayList<Callable> list = new ArrayList<>();

		list.add(cal2);
		list.add(cal3);
		list.add(cal4);
		list.add(cal5);
		list.add(cal1);


		for (Callable c : list) {
			service.submit(c);
		}


		es.shutdown();//这里就是只有当线程池没有了正在执行的线程，且任务队列内没有任务后，可平滑关闭。

		int times = TIMES;
		while (true) {
			try {
				if (es.awaitTermination(1, TimeUnit.SECONDS) || times <= 0) {//此处表示，每秒检测下原线程池是否平滑关闭，或者当前是否满60秒了，满足条件，则跳到下面输出结果
					break;
				}
				times--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < 5; i++) {
			System.out.println(service.poll().get());
		}

	}
}
