package com.st.practice.thread.callable;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: st
 * @date: 2023/3/1 16:17
 * @version: 1.0
 * @description:
 */
public class TestDemo3 {

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

			for (Callable c : list) {
				System.out.println(service.take().get());//take：有就返回，没有就等到有再返回；poll无参：直接返回，没执行出结果则直接返回null；poll有参：有就返回，没有就等到入参定义的时间后返回执行结果，若还是没有执行到，则直接返回null
			}
		}

}
