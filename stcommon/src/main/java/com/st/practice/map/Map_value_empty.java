package com.st.practice.map;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2022/3/15 19:22
 * @version: 1.0
 * @description:
 */
public class Map_value_empty {

  public static void main(String[] args) {
	  Map<String, String> map = new HashMap<>();//
    map.put("1", "");
    System.out.println(map.get("1").length());
    System.out.println("".equals(""));
    System.out.println(map.get("1")+"xxx");

    String x = "a.b";
    System.out.println(x.contains("\\."));
    System.out.println(x.contains("."));
  }
}
