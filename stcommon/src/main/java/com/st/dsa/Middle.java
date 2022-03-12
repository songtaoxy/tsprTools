package com.st.dsa;

import cn.hutool.core.util.RandomUtil;

import java.util.Arrays;
import java.util.stream.Collector;

/**
 * @author: st
 * @date: 2022/3/13 01:37
 * @version: 1.0
 * @description:
 */
public class Middle {

  public static void main(String[] args) {
    int[] ints = RandomUtil.randomInts(3); //
    int[] ints4 = RandomUtil.randomInts(4); //


    //[1, 2, 0]
    System.out.println(Arrays.toString(ints));
    //[3, 2, 1, 0]
    System.out.println(Arrays.toString(ints4));



  }
}
