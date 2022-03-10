package com.st.practice.sort;

import cn.hutool.core.util.RandomUtil;

import java.util.Arrays;

/**
 * @author: st
 * @date: 2022/3/10 03:30
 * @version: 1.0
 * @description:
 */
public class MergeSort2 {
  public static void merge(int[] a, int low, int mid, int high) {
    int[] temp = new int[high - low + 1];
    int i = low;
    int j = mid + 1;
    int k = 0;
    while (i <= mid && j <= high) {
      if (a[i] < a[j]) {
        temp[k++] = a[i++];
      } else {
        temp[k++] = a[j++];
      }
    }
    while (i <= mid) {
      temp[k++] = a[i++];
    }
    while (j <= high) {
      temp[k++] = a[j++];
    }
    for (int k2 = 0; k2 < temp.length; k2++) {
      a[k2 + low] = temp[k2];
    }
  }

  public static void mergeSort(int[] a, int low, int high) {
    int mid = (low + high) / 2;
    if (low < high) {
      mergeSort(a, low, mid);
      mergeSort(a, mid + 1, high);
      merge(a, low, mid, high);
      System.out.println(Arrays.toString(a));
    }
  }

  public static void main(String[] args) {
    // int ints[] = {51, 46, 20, 18, 65, 97, 82, 30, 77, 50};
    int[] ints = RandomUtil.randomInts(10);
    mergeSort(ints, 0, ints.length - 1);
    System.out.println("排序结果：" + Arrays.toString(ints));
  }
}
