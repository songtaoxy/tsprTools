package com.st.practice.threadlocal;

/**
 * @author: st
 * @date: 2022/3/7 12:43
 * @version: 1.0
 * @description:
 */
public class CountDemo {
  private static final int COUNT_BITS = Integer.SIZE - 3;
  private static final int CAPACITY = (1 << COUNT_BITS) - 1;

  // runState is stored in the high-order bits
  private static final int RUNNING = -1 << COUNT_BITS;
  private static final int SHUTDOWN = 0 << COUNT_BITS;
  private static final int STOP = 1 << COUNT_BITS;
  private static final int TIDYING = 2 << COUNT_BITS;
  private static final int TERMINATED = 3 << COUNT_BITS;

  public static void main(String[] args) {
    System.out.println(COUNT_BITS); //
    System.out.println(CAPACITY); //
    System.out.println(RUNNING); //
    System.out.println(SHUTDOWN); //
    System.out.println(STOP); //
    System.out.println(TIDYING); //
    System.out.println(TERMINATED); //
  }
}
