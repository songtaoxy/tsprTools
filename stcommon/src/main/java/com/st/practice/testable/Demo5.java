package com.st.practice.testable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: st
 * @date: 2022/3/19 00:41
 * @version: 1.0
 * @description:
 */
public class Demo5 {
  public static void main(String[] args) {
	  //
	  HashMap<String, String> map = new HashMap<>();
    map.put("k", "v");

	  String next = map.keySet().iterator().next();
	  String value = map.get(next);
    System.out.println(next+"==="+value);


	  ArrayList<String> strings = new ArrayList<>();
    strings.add("a");
	  String s = strings.get(0);
    System.out.println(s);
  }
}
