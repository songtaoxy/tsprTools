package com.st.api.practice.thread;

import java.io.IOException;

/**
 * @author: st
 * @date: 2021/7/16 00:36
 * @version: 1.0
 * @description:
 */
public class Demo_1 {
  public static void main(String[] args) {
    MyThread_1 myThread1 = new MyThread_1();
    //myThread1.setDaemon(true);
    myThread1.start();
    try {
      System.in.read(); // 接受输入，使程序在此停顿，一旦接收到用户输入，main线程结束，守护线程自动结束
    } catch (IOException ex) {
    }
  }
}

class MyThread_1 extends Thread {

  @Override
  public void run() {
    System.out.println("Thread.currentThread().isDaemon() = " + Thread.currentThread().isDaemon());
  }
}
