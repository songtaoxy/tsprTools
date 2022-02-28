package com.st.practice.jprofile;

/**
 * @author: st
 * @date: 2022/2/25 01:26
 * @version: 1.0
 * @description:
 */
public class JprofileDemo {
  String x = "100";

  public static void main(String[] args) {
    JprofileDemo jprofileDemo = new JprofileDemo();
    /*
    while (true) {
      System.out.println("hi");
    } //
    */

    jprofileDemo.m2();
  }

  public void m2() {
    System.out.println(x);
    x = "300";
    System.out.println(x);
  }
}
