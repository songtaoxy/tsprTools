package com.st.practice.com;

/**
 * @author: st
 * @date: 2022/3/4 20:37
 * @version: 1.0
 * @description:
 */
public class ThreadSortDemo implements Runnable {

  private String string;

  public ThreadSortDemo(String s) {
    this.string = s;
  }

  public void printAlph() {

    System.out.println(string);
  }

  /** @see Thread#run() */
  @Override
  public void run() {
    //ThreadSortDemo threadSortDemo = new ThreadSortDemo(string);
    //threadSortDemo.printAlph();
    printAlph();
  }

  public static void main(String[] args) throws InterruptedException {

    for (int i = 0; i < 10; i++) {
      Thread thread = new Thread(new ThreadSortDemo("a"));
      Thread thread2 = new Thread(new ThreadSortDemo("b"));
      Thread thread3 = new Thread(new ThreadSortDemo("c"));
      thread.start();
      thread.join();
      thread2.start();
      thread2.join();
      thread3.start();
      thread3.join();
    }
  }
}
