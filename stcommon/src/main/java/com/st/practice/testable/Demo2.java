package com.st.practice.testable;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author: st
 * @date: 2022/3/17 19:40
 * @version: 1.0
 * @description:
 */
public class Demo2 {

  public String sub(String str, int s, int e) {

    return str.substring(s, e);
  }

  public String sub2(String str, int s, int e) {
    return this.sub(str, s, e);
  }

  public static void main(String[] args) {
    /*  String x = "xxx100";
    String y = null;
    y = (String) x;
    System.out.println(y);*/

    String str = "c.d";

    // System.out.println(str.contains("."));
    // System.out.println("a".contains("."));

    String[] split = str.split("\\.");
    System.out.println(split[0]);
    System.out.println(str.substring(str.indexOf(".") + 1, str.length()));
    // Arrays.stream(split).forEach(System.out::println);

    // Preconditions.checkArgument("aa".equals("bb"), "a必须是a");

    Map<String, List<String>> map = new HashMap();
    List<String> fields = new ArrayList();

    fields.add("100");
    fields.add("xxx");
    fields.add("bb.aa");
    map.put("k", fields);
    System.out.println(map);

    Demo2 demo = new Demo2();

    Map<String, List<String>> map2 = new HashMap<String, List<String>>();

    demo.linkAttr("hi", "a.b.d.cc", map2);
    System.out.println(map2);
  }

  Map<String, List<String>> map = new HashMap<String, List<String>>();
  List<String> fields = new ArrayList<String>();

  public void linkAttr(String entity, String string, Map<String, List<String>> map2) {

    if (string.contains(".")) {

      String fieldStr = string.split("\\.")[0];
      String tail = string.substring(string.indexOf(".") + 1, string.length());

      String uri = entity;

      if (!(tail.contains("."))) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(tail);
        map2.put(uri, strings);
      }
      entity = uri + entity;
      linkAttr(entity, tail, map2);
    }
  }
}
