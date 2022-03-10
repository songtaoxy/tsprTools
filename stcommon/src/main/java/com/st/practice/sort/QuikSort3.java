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
public class QuikSort3 {
	public static int partition(int[] array, int low, int high) {
		int pivot = array[high];
		int pointer = low;
		for (int i = low; i < high; i++) {
			if (array[i] <= pivot) {
				swip(array, i, pointer);
				pointer++;
			}
			System.out.println(Arrays.toString(array));
		}
		swip(array, pointer, high);
		return pointer;
	}

	public static void quickSort(int[] array, int low, int high) {
		if (low < high) {
			int position = partition(array, low, high);
			quickSort(array, low, position - 1);
			quickSort(array, position + 1, high);
		}
	}

	public static void swip(int a, int b) {
		int temp = a;
		a = b;
		b = temp;
	}

	//
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
