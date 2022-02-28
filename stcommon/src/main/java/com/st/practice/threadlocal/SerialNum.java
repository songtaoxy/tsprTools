package com.st.practice.threadlocal;

import com.google.errorprone.annotations.Var;
import com.sun.tools.javadoc.Start;
import lombok.Getter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * @author: st
 * @date: 2022/2/21 17:28
 * @version: 1.0
 * @description:
 */
public class SerialNum {

  // The next serial number to be assigned
  private static int nextSerialNum = 0;

  public static synchronized int getNextSerialNum() {
    return nextSerialNum;
  }

  private static ThreadLocal serialNum =
      new ThreadLocal() {
        protected synchronized Object initialValue() {
          return new Integer(nextSerialNum++);
        }
      };

  ThreadLocal serial =
      new ThreadLocal() {
        protected synchronized Object initialValue() {
          return new Integer(nextSerialNum++);
        }
      };

  public static int get() {
    // return ((Integer) (serialNum.get())).intValue();
    return ((Integer) (serialNum.get()));
  }

  public int get2() {
    // return ((Integer) (serialNum.get())).intValue();
    return (int) new SerialNum().serial.get();
  }

  // (1)
  static final ThreadPoolExecutor poolExecutor =
      new ThreadPoolExecutor(2, 5, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

  public static void main(String[] args) throws InterruptedException {
     //t1();
    t2();
    // t3();
  }

  public static void t1() {
    // Thread.sleep(5000 * 4);
    for (int i = 0; i < 10; ++i) {
      poolExecutor.execute(
          new Runnable() {
            public void run() {
              System.out.println(
                  Thread.currentThread().getName()
                      + "--->"
                      + SerialNum.get()
                      + "--->"
                      + serialNum.get()
                      + "--->"
                      + serialNum
                      + "--->"
                      + nextSerialNum);
              // 如果不remove, 因为使用的是线程池, coresize 中的变量不变
              // serialNum.remove();
            }
          });
    }
    System.out.println("pool execute over");
  }

  public static void t2() {
    // Thread.sleep(5000 * 4);
    for (int i = 0; i < 10; ++i) {
      poolExecutor.execute(
          new Runnable() {
            public void run() {
              System.out.println(
                  Thread.currentThread().getName()
                      + "--->"
                      + SerialNum.get()
                      + "--->"
                      + serialNum.get()
                      + "--->"
                      + serialNum
                      + "--->"
                      + nextSerialNum);
                // 如果不remove, 因为使用的是线程池, coresize 中的变量不变
                // remove后, 线程池中的线程信息清除
                serialNum.remove();
            }
          });
    }
    System.out.println("pool execute over");
  }

  public static void t3() throws InterruptedException {
    // Thread.sleep(5000 * 4);
    for (int i = 0; i < 10; ++i) {
      new Thread() {
        public void run() {
          SerialNum s = new SerialNum();
          System.out.println(
              Thread.currentThread().getName()
                  /*+ "--->"
                  + s.get2()*/
                  + "--->"
                  + s.serial.get()
                  + "--->"
                  + s.serial
                  + "--->"
                  + nextSerialNum
                  + "--->"
                  + getNextSerialNum());
          // new SerialNum().serial.remove();
        }
      }.start();
    }
    ;
    System.out.println("pool execute over");
  }
}
