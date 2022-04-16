package com.st.dsa;

import java.util.Arrays;

/**
 * @author: st
 * @date: 2022/4/12 12:22
 * @version: 1.0
 * @description:
 */
public class MergeTwoArray {

  public static void merge(int[] A, int m, int[] B, int n) {

    int s1 = 0;
    int s2 = 0;
    int[] sorted = new int[m + n];
    int index;

    while (s1 < m || s2 < n) {
      if (s1 == m) {
        index = B[s2++];

      } else if (s2 == n) {
        index = A[s1++];
      } else if (A[s1] < B[s2]) {
        index = A[s1++];
      } else {
        index = B[s2++];
      }
      sorted[s1 + s2 - 1] = index;
    }

    for (int i = 0; i != s1 + s2; i++) {
      A[i] = sorted[i];
    }
  }

  public static void main(String[] args) {

    // 原始; 扩容后, 见下面的
    // int[] a = new int[] {4, 5, 6};
    int[] a = new int[] {4, 5, 6, 0, 0, 0};
    int[] b = new int[] {1, 2, 3};

    merge(a, 3, b, b.length);
    System.out.println(a);
    Arrays.stream(a).forEach(System.out::println);
  }
}
