package com.st.practice.sort;

import cn.hutool.core.util.RandomUtil;

import java.util.Arrays;

/**
 * 数组不变, 在同一个array中, 实现排序.
 *
 * @author: st
 * @date: 2022/3/10 02:02
 * @version: 1.0
 * @description:
 */
public class QuikSort2 {
  public static int partition(int[] array, int low, int high) {

    // 取最后一个元素作为中心元素
    int pivot = array[high];

    // 理解的关键5: 如果遇到较大的数, 指针不变; 遇到小数, 则交换后, 才移动指针.
    // 定义指向比中心元素大的指针，首先指向第一个元素
    int pointer = low;
    // 理解的关键1: 从头到尾, 挨个扫描数组中的元数, 只要扫描到的元数小于pivot,则将这个较小的数和前面指针(较大的数)交换
    // 理解的关键2: 经过一轮循环,比中心元素大的放在右边，比中心元素小的放在左边

    for (int i = low; i < high; i++) {
      // 理解的关键3: 扫描到的数, 如果较大, 则略过: 指针保持不变, 继续循环(扫描), 直到找到较小的数(小于pivot的数)
      if (array[i] <= pivot) {
        // 理解的关键4: 将比中心元素小的元素和指针指向的元素交换位置
        // 如果第一个元素比中心元素小，这里就是自己和自己交换位置，指针和索引都向下一位移动
        // 如果元素比中心元素大，索引向下移动，指针指向这个较大的元素，直到找到比中心元素小的元素，并交换位置，指针向下移动
        swip(array, i, pointer);
        // 理解的关键5: 如果遇到较大的数, 指针不变; 遇到小数, 则交换后, 才移动指针.
        pointer++;
      }
      System.out.println(Arrays.toString(array));
    }
    // 理解的关键6: 将中心元素和指针指向的元素交换位置. 中心左边的,都是较小的, 右边都是大的.
    swip(array, pointer, high);

    // 理解的关键7: 返回中心元素所在的位置. 为后面的递归用.
    return pointer;
  }

  public static void quickSort(int[] array, int low, int high) {
    if (low < high) {
      // 获取划分子数组的位置
      int position = partition(array, low, high);
      // 左子数组递归调用
      quickSort(array, low, position - 1);
      // 右子数组递归调用
      quickSort(array, position + 1, high);
    }
  }

  // 交换带个元素的值. 直接的int数字,  或int的数组.
  // 传递的过来的: array[i], 是传值, 而非引用.
  // 栈中, ab的指向变了, 但array本身没有变化.
  public static void swip(int a, int b) {
    int temp = a;
    a = b;
    b = temp;
  }

  // array 本身发生了变化. 排序, 是在array本身中进行排序.
  public static void swip(int[] arry, int indexa, int indexb) {
    int temp = arry[indexa];
    arry[indexa] = arry[indexb];
    arry[indexb] = temp;
  }

  public static void main(String[] args) {
    int[] ints = RandomUtil.randomInts(10);
    quickSort(ints, 0, ints.length - 1);
    System.out.println("排序后的结果");
    System.out.println(Arrays.toString(ints));
  }
}
