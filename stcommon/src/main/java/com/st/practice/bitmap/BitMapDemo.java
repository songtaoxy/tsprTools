package com.st.practice.bitmap;

import java.util.BitSet;

/**
 * @author: st
 * @date: 2022/3/9 12:38
 * @version: 1.0
 * @description:
 */
public class BitMapDemo {
  public static void main(String[] args) {
    int[] array = new int[] {1, 2, 3, 22, 0, 3,100};
    BitSet bitSet = new BitSet();
    // 将数组内容组bitmap
    for (int i = 0; i < array.length; i++) {
      bitSet.set(array[i], true);
    }

    System.out.println(bitSet.size());

    // 判断某个数字是否存在
    System.out.println(bitSet.get(3));
    System.out.println(bitSet.get(30));

    System.out.println("1 ,二进制 ["+Integer.toBinaryString(1)+"]");
    System.out.println("-1,二进制 ["+Integer.toBinaryString(-1)+"]");
    System.out.println(Integer.MAX_VALUE);
    System.out.println(Integer.MIN_VALUE);
    System.out.println(Integer.toBinaryString(2147483647));
    System.out.println(Integer.toBinaryString(-2147483648));
  }
}
