package com.st.api.practice.thread;

//启动类
public class Run {
	public static void main(String[] args) {
		MyThread myThread1 = new MyThread();
		//初始化Thread对象，方便调用start();
		//此时myThread作为参数传入Thread中，其实是myThread委托thread去执行；
		Thread thread = new Thread(myThread1);
		//初始化自定义线程名称
		thread.setName("C");
		//启动线程
		thread.start();

	}
}
class MyThread extends Thread {

	public MyThread(){
		System.out.println("当前线程的名字-current："+Thread.currentThread().getName());
		System.out.println("当前线程的名字-this："+this.getName());
	}
	@Override
	public void run(){
		System.out.println("当前线程的名字："+Thread.currentThread().getName()+"   run=="+Thread.currentThread().isAlive());
		System.out.println("当前线程的名字："+this.getName()+"  run=="+this.isAlive());
	}
}
