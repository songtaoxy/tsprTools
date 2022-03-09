package com.st.practice.com;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: st
 * @date: 2022/3/8 01:46
 * @version: 1.0
 * @description:
 */
public class TasksTestLock extends Thread {
  private static Lock lock = new ReentrantLock();
  private static Condition condition = lock.newCondition();
  private static int num = 1;
  private int id;

  public TasksTestLock(int id) {
    this.id = id;
  }

  @Override
  public void run() {
    while (num <= 12) {

      lock.lock();

      System.out.println("Thread" + id + " num:" + num);
      num++;

      condition.signal();
      try {
        condition.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      lock.unlock();
    }
  }

  public static void main(String[] args) {
    Thread thread0 = new TasksTestLock(0);
    Thread thread1 = new TasksTestLock(1);
    Thread thread2 = new TasksTestLock(2);

    ExecutorService exec = Executors.newFixedThreadPool(3);

    exec.submit(thread0);
    exec.submit(thread1);
    exec.submit(thread2);

    exec.shutdown();
  }
}
