package com.st.practice.exception;

/**
 * @author: st
 * @date: 2021/11/13 12:06
 * @version: 1.0
 * @description:
 */
public class ExceptionDemo {

  void em1_2(int div) {

    System.out.println(100 / div);
    // 异常之后的代码, 能继续执行
    System.out.println("异常之后的代码继续执行了");
  }

  void em1_3(int div) {

    try {
      System.out.println(100 / div);
    } catch (Exception e) {
      throw e;
    }

    // 异常之后的代码, 能继续执行
    System.out.println("异常之后的代码继续执行了");
  }

  void em1_4(int div) throws Exception {

    try {
      System.out.println(100 / div);
    } catch (Exception e) {
      throw new Exception(e);
    }

    // 异常之后的代码, 能继续执行
    System.out.println("异常之后的代码继续执行了");
  }

  public static void em3(String str, int index) throws Exception {
    try {
      str.substring(index);
    } catch (Exception e) {
      throw new Exception("数组越界", e);
    }
    System.out.println("contiue-3");
  }

  public static void em4() {
    try {
      em3("hi", 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("contiue-4");
  }

  public static void em5(String str, int index) {
    str.substring(index);
  }

  public static void em6() {
    em5("hi", 0);
  }

  public static void em7(String str, int index) throws Exception {
    str.substring(index);
  }

  public static void em8() {
    try {
      em7("hi", 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
