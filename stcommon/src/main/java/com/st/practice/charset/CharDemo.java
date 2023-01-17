package com.st.practice.charset;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author: st
 * @date: 2022/3/9 22:34
 * @version: 1.0
 * @description:
 */
public class CharDemo {
  public static void main(String[] args) {
    String s = "君"; //
    byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
    for (int i = 0; i < bytes.length; i++) {
      System.out.println(bytes[i]);
    }
  }
}
