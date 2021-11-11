package com.st.api.practice.string;

/**
 * @author: st
 * @date: 2021/9/30 19:09
 * @version: 1.0
 * @description:
 */
public class StringTest {
  String str = "abc";
  char[] chars = {'a', 'b', 'c'};

  public void changes(String newStr, char[] chars) {
    newStr = "good";
    chars[0] = 'x';
  }

  public static void main(String[] args) {
    StringTest st = new StringTest();

    st.changes(st.str, st.chars);

    System.out.println(st.str);
    System.out.println(st.chars);
  }
}
