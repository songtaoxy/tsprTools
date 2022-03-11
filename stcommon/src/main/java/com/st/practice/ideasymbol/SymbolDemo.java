package com.st.practice.ideasymbol;

import cn.hutool.core.util.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author: st
 * @date: 2022/3/11 12:24
 * @version: 1.0
 * @description:
 */
public class SymbolDemo {

  static class Inner {

    public static void main(String[] args) {
      int[] ints = RandomUtil.randomInts(10);
      //Arrays.stream(ints).forEach(System.out::println);


      String s = RandomUtil.randomString(10);
      List<String> strings = RandomUtil.randomEleList(new ArrayList<String>(), 10);
      strings.stream().forEach(System.out::println);
    }
  }
}
