package com.st.practice.type;



/**
 * @author: st
 * @date: 2022/4/7 13:38
 * @version: 1.0
 * @description:
 */
public class ValueAndRef {

  public static void main(String[] args) {
    int a = 1;
    int b = 1;
    Integer c = new Integer(1);
    Integer d = new Integer(1);
    Integer f = 1;
    Integer e = 1;

    System.out.println(a == b); // true
    System.out.println(a == 1); // true
    System.out.println(1 == 1); // ture
    System.out.println(c == d); // fale
    System.out.println(c == 1); // true
    System.out.println(c == a); // true
    System.out.println(c == f); // false
    System.out.println(e == f); // true

    System.out.println("a" == "a"); // true
    String s1 = "a";
    String s2 = "a";
    String s3 = new String("a");
    System.out.println(s1 == s2); // true
    System.out.println(s1 == "a"); // true
    System.out.println(s3 == s1); // fale
    System.out.println(s3 == "a"); // fale
  }
}
