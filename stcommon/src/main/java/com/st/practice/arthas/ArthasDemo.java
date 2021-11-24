package com.st.practice.arthas;

import com.st.practice.com.Person;

/**
 * @author: st
 * @date: 2021/11/24 23:43
 * @version: 1.0
 * @description:
 */
public class ArthasDemo {

  static Person tom = new Person("Tom", 20);


  public static void main(String[] args) {

    tom.m1();

    String s = Person.m2();

    while (true) {
      System.out.println("...");
    }
  }
}
