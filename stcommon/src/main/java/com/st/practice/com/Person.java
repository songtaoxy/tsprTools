package com.st.practice.com;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author: st
 * @date: 2021/11/24 23:44
 * @version: 1.0
 * @description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {

  private String name;
  private int age;
  private Map map;

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
    return param + "add====";
  }

  public void m_pub() {
    System.out.println("");
    Person person = new Person();
  }

  protected void m_pro() {
    System.out.println("");
  }

  void m_default() {
    System.out.println("");
  }

  private void m_pri() {
    System.out.println("");
  }
}
