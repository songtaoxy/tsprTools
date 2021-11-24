package com.st.practice.com;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author: st
 * @date: 2021/11/24 23:44
 * @version: 1.0
 * @description:
 */
@AllArgsConstructor
@NoArgsConstructor
public class Person {

  private String name;
  private int age;

  public String m1() {
    System.out.println("Person:m1");
    return "m1";
  }

  public static String m2() {
    System.out.println("Person:m2");
    return "m2";
  }

  public String m3(String param) {
    System.out.println("Person:param");
    return param+"add====";
  }
}
