package com.st.practice.arthas;

import com.alibaba.fastjson.JSONObject;
import com.st.practice.com.Person;

import java.util.HashMap;

/**
 * @author: st
 * @date: 2021/11/24 23:43
 * @version: 1.0
 * @description:
 */
public class ArthasDemo extends Person {

  static Person tom = new Person("Tom", 20, new HashMap(), new JSONObject());

  public static void main(String[] args) {

    tom.m1();

    String s = Person.m2();

    while (true) {
      System.out.println("...");

      m();
    }
  }

  public static void m() {
    Person person = new Person();
    person.m_pub();


  }
}
