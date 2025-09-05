package com.st.practice.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2022/1/19 15:41
 * @version: 1.0
 * @description:
 */
public class HashMapTreeifyDemo {
  public static void main(String[] args) {

    Map<Key, Integer> map = new HashMap<>();
    for (int i = 0; i < 10000; i++) {

      System.out.println(new Key());
      System.out.println(new Key().hashCode());
      System.out.println(hash(new Key()));
      map.put(new Key(i + ""), i);
    }

    // 断点打折这里
    System.out.println(map);
  }


  @NoArgsConstructor
  @AllArgsConstructor
  static class Key {
    String name;

    @Override
    public int hashCode() {

      return 10;
    }
  }

  static final int hash(Object key) {
    int h;
    int x = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    System.out.println("......" + x);

    // return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    return x;
  }
}
