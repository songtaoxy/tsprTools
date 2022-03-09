package com.st.practice.bitmap;

import cn.hutool.core.util.RandomUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2022/3/9 14:22
 * @version: 1.0
 * @description:
 */
public class BitMapDemo02 {
  /**
   * @param arr
   * @param number
   * @return 返回值类型, 可以根据情况返回:某个数是否存在; 重复的数, 及其数量
   */
  public static Set test(int[] arr, int number) {

    // 存放每个数及其数量. {value, count}
    HashMap<Integer, Integer> mapStatic = new HashMap<>();
    // 避免返回重复的数，存在Set里
    Set<Integer> duplicatedNumber = new HashSet<>();
    // 重复的数及其数量. {value, count}
    // HashMap<Integer, Integer> duplicateMap = new HashMap<Integer, Integer>();

    // 容量: 64bit(default), 自动扩容, 每次*2
    BitSet bitSet = new BitSet();
    // BitSet bitSet = new BitSet(Integer.MAX_VALUE);

    for (int i = 0; i < arr.length; i++) {
      int value = arr[i];
      // 判断该数是否存在bitSet里
      if (bitSet.get(value)) {

        mapStatic.put(value, mapStatic.get(value) + 1);
        duplicatedNumber.add(value);

      } else {
        bitSet.set(value, true);
        mapStatic.put(value, 1);
      }
    }

    Map<Integer, Integer> duplicateMap =
        mapStatic.entrySet().stream()
            .filter(map -> map.getValue() > 1)
            .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

    System.out.println("数即其数量:" + mapStatic);
    System.out.println("重复的数及其数量" + duplicateMap);
    System.out.println("重复的数:" + duplicatedNumber);
    System.out.println(
        number
            + "===在给定的数组中存在吗?==="
            + "["
            + bitSet.get(number)
            + "],数量为:["
            + (bitSet.get(number) ? mapStatic.get(number) : 0)
            + "]");
    return duplicatedNumber;
  }

  // 测试
  public static void main(String[] args) {
    /*int[] t = {1, 2, 3, 4, 5, 6, 7, 8, 3, 4, 4, 4, 9, 100, 100};
    Set t2 = test(t, 100);
    System.out.println("===============================================================");
    Set t3 = test(t, 300);
*/
    int[] ints = RandomUtil.randomInts(1000000);
    //Arrays.stream(ints).forEach(System.out::println);

    test(ints, 100);
  }
}
