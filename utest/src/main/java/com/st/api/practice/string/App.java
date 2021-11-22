package com.st.api.practice.string;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: st
 * @date: 2021/10/11 20:37
 * @version: 1.0
 * @description:
 */
public class App {

  public static void main(String[] args) throws IOException {

    String[] strs = args;
    strs = new String[] {"a", "b"};
    int length = strs.length;

    String hello = "param1_Arthas";


    ArrayList<String> strings = new ArrayList<>();
    strings.add("hello");
    strings.add("Arthas");


    ArrayList<String> strings2 = new ArrayList<>();
    strings2.add("hello");
    strings2.add("good");


    while (true) {
      // boolean contains = StringUtils.contains(hello, "Arthas");
      // boolean containsx = StringUtils.contains(hello, "good");



      strTest("hello", "Arthas");
      strTest2(strings);
      System.out.println("Arthas");

      strTest("hello", "good");
      strTest2(strings2);
      System.out.println("good");
    }
  }

  public static String strTest(String a, String b) {

    a.getBytes(StandardCharsets.UTF_8);
    b.getBytes(StandardCharsets.UTF_8);
    return "return";

  }
  public static String strTest2(List<String> list) {

    list.get(0).getBytes(StandardCharsets.UTF_8);
    list.get(1).getBytes(StandardCharsets.UTF_8);
    return "return";

  }

}
