package com.st.practice.sort;

import cn.hutool.core.util.RandomUtil;

import java.util.Arrays;

/**
 * 全部无序->外部无序, 内部有序->归并排序, 全部有序.
 *
 * @author: st
 * @date: 2022/3/10 03:30
 * @version: 1.0
 * @description:
 */
public class MergeSort {

  /**
   * 左右两边已经是有序的了, 即左右两边有序, 进行排序.
   *
   * @param a
   * @param low
   * @param mid
   * @param high
   */
  public static void merge(int[] a, int low, int mid, int high) {
    int[] temp = new int[high - low + 1];
    int i = low; // 左指针
    int j = mid + 1; // 右指针
    int k = 0;

    // 左右两边分别从开始位置取出数据, 进行比较.
    // 把较小的数先移到新数组中. 左右两边, 剩下的, 都是较大的; 排序后, 直接拼接到数组上即可.
    while (i <= mid && j <= high) {
      if (a[i] < a[j]) {
        temp[k++] = a[i++];
      } else {
        temp[k++] = a[j++];
      }
    }
    // 把较小的数先移到新数组中. 左右两边, 剩下的, 都是较大的; 排序后, 直接拼接到数组上即可.
    // 把左边剩余的数移入数组
    while (i <= mid) {
      temp[k++] = a[i++];
    }
    // 把较小的数先移到新数组中. 左右两边, 剩下的, 都是较大的; 排序后, 直接拼接到数组上即可.
    // 把右边边剩余的数移入数组
    while (j <= high) {
      temp[k++] = a[j++];
    }
    // 把新数组中的数覆盖nums数组;
    // 将新数组中, 排序好的数组, 重新拷贝到原始数组中, 完成排序.
    for (int k2 = 0; k2 < temp.length; k2++) {
      a[k2 + low] = temp[k2];
    }
  }

  public static void mergeSort(int[] a, int low, int high) {
    int mid = (low + high) / 2;
    if (low < high) {
      // 左边
      mergeSort(a, low, mid);
      // 右边
      mergeSort(a, mid + 1, high);

      // 左右归并. 到这个方法, 说明左右已经是有序的了.
      // 递归终止条件: 只有两个元素, 左右各一个.完成排序, 返回,返回,...
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
