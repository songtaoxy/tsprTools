package com.st.practice.sort;

import ch.qos.logback.core.net.SyslogOutputStream;

import java.util.Date;

/**
 * @author: st
 * @date: 2022/3/4 17:16
 * @version: 1.0
 * @description:
 */
public class CountDupicated {

  public static void main(String[] args) {

    System.out.println((7 | (1 << 3)));

    long time = new Date().getTime();

    // int数组, 长度1亿
    int[] arr = new int[100000000];
    // 每个元素不重复
    for (int i = 0; i < arr.length; i++) {
      arr[i] = i + 1;
    }

    // 原本为100000000, 设为2020, 即和arr[2019]重复
    arr[2020] = 2020;
    int min = arr[0];
    int max = arr[0];

    // 获取最大, 最小值
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] < min) min = arr[i];
      if (arr[i] > max) max = arr[i];
    }

    // 申请长度
    byte[] bucket = new byte[(max - min) / 8 + 1];
    int bucketSize = bucket.length;
    for (int i = 0; i < arr.length; i++) {
      int num = arr[i];
      int j = (num - min) / 8;
      int k = (num - min) % 8;
      if (((bucket[j] >> k) & 1 ) > 0) { // 重复了
        System.out.println("Number of repeats：" + num);
        break;
      } else {
        bucket[j] |= (1 << k);
      }
    }

    long time2 = new Date().getTime();
    System.out.println("millisecond:" + (time2 - time));
  }
}
