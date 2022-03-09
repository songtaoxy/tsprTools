package com.st.practice.bitmap;

import java.util.BitSet;
import java.util.Set;

/**
 * @author: st
 * @date: 2022/3/9 15:47
 * @version: 1.0
 * @description:
 */
public class BitMapDemo03 {

  public static boolean exists(int[] intArray, int number) {

    BitSet bitSet = new BitSet();
    for (int i = 0; i < intArray.length; i++) {
      bitSet.set(intArray[i]);
    }

    System.out.println(number + "===在给定的数组中存在吗?===" + "[" + bitSet.get(number) + "]");
    return bitSet.get(number);
  }

  // 测试
  public static void main(String[] args) {
    int[] t = {1, 2, 3, 4, 5, 6, 7, 8, 3, 4, 4, 4, 9, 100, 100};

    exists(t, 200);
    exists(t, 100);
  }
}
