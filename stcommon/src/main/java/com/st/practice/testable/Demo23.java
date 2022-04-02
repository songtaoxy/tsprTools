package com.st.practice.testable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: st
 * @date: 2022/3/17 19:40
 * @version: 1.0
 * @description:
 */
public class Demo23 {

  Map<String, List<String>> map = new HashMap<String, List<String>>();

  public String sub(String str, int s, int e) {

    return str.substring(s, e);
  }

  public String sub2(String str, int s, int e) {
    return this.sub(str, s, e);
  }

  public static void main(String[] args) {

    Demo23 demo = new Demo23();

    demo.linkAttr("hi", "a.b.d.cc");
    System.out.println(demo.map);
  }

  public void linkAttr(String entity, String string) {

    if (string.contains(".")) {

      String fieldStr = string.split("\\.")[0];
      String tail = string.substring(string.indexOf(".") + 1, string.length());

      String uri = entity;

      if (!(tail.contains("."))) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(tail);
        map.put(uri, strings);
      }
      entity = uri + entity;
      linkAttr(entity, tail);
    }
  }
}
