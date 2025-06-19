package com.st.dsa.sort;

import java.util.Arrays;

/**
 * 归并排序
 *
 * @param arr 输入的整数数组
 * @return 返回排序后的整数数组
 */
public class MergeSort {

    public static int[] mergeSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return arr;  // 如果数组为空或长度为1，直接返回
        }
        mergeSort(arr, 0, arr.length - 1);  // 对整个数组进行排序
        return arr;
    }

    private static void mergeSort(int[] arr, int leftStart, int rightEnd) {
        if (leftStart >= rightEnd) {
            return;  // 如果子数组只有一个元素，已经排序好
        }

        int middle = (leftStart + rightEnd) / 2;

        // 递归排序左右两部分
        mergeSort(arr, leftStart, middle);  // 对左半部分排序
        mergeSort(arr, middle + 1, rightEnd);  // 对右半部分排序

        // 合并已排序的两部分
        mergeHalves(arr, leftStart, middle, rightEnd);
    }

    /**
     * 合并两个已排序的子数组
     *
     * @param arr 输入的数组
     * @param leftStart 左部分的起始索引
     * @param middle 中间索引
     * @param rightEnd 右部分的结束索引
     */
    private static void mergeHalves(int[] arr, int leftStart, int middle, int rightEnd) {
        // 左右子数组的大小
        int leftSize = middle - leftStart + 1;
        int rightSize = rightEnd - middle;

        // 创建临时数组来存储左右子数组
        int[] left = new int[leftSize];
        int[] right = new int[rightSize];

        // 将左右子数组填充
        System.arraycopy(arr, leftStart, left, 0, leftSize);
        System.arraycopy(arr, middle + 1, right, 0, rightSize);

        int leftIndex = 0, rightIndex = 0, mergedIndex = leftStart;

        // 合并过程
        while (leftIndex < leftSize && rightIndex < rightSize) {
            if (left[leftIndex] <= right[rightIndex]) {
                arr[mergedIndex] = left[leftIndex];
                leftIndex++;
            } else {
                arr[mergedIndex] = right[rightIndex];
                rightIndex++;
            }
            mergedIndex++;
        }

        // 如果左部分还有剩余元素，复制到原数组
        while (leftIndex < leftSize) {
            arr[mergedIndex++] = left[leftIndex++];
        }

        // 如果右部分还有剩余元素，复制到原数组
        while (rightIndex < rightSize) {
            arr[mergedIndex++] = right[rightIndex++];
        }
    }

    // 测试代码
    public static void main(String[] args) {
        int[] arr = {5, 2, 9, 1, 5, 6};
        int[] sortedArr = mergeSort(arr);
        System.out.println("Sorted array: " + Arrays.toString(sortedArr));
    }
}
