package com.st.dsa.sort;

public class QuickSort02 {

    // 快速排序方法
    public static void quickSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    // 快速排序的核心递归方法
    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // 找到基准元素的正确位置
            int pivotIndex = partition(arr, low, high);

            // 递归排序基准元素左边和右边的部分
            quickSort(arr, low, pivotIndex - 1);  // 排序基准左边
            quickSort(arr, pivotIndex + 1, high); // 排序基准右边
        }
    }

    // 分割方法：返回基准元素的索引
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];  // 选择最后一个元素作为基准元素
        int i = low - 1;  // i 用来追踪小于基准元素的区域

        // 遍历数组并交换小于基准的元素到左边
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);  // 交换 arr[i] 和 arr[j]
            }
        }

        // 将基准元素交换到正确位置
        swap(arr, i + 1, high);  // 交换基准元素与 arr[i + 1]
        return i + 1;  // 返回基准元素的正确位置
    }

    // 交换方法，用来交换数组中的两个元素
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // 测试代码
    public static void main(String[] args) {
        int[] arr = {5, 2, 9, 1, 5, 6};
        System.out.println("Before sorting: " + java.util.Arrays.toString(arr));

        quickSort(arr);  // 调用快速排序

        System.out.println("After sorting: " + java.util.Arrays.toString(arr));
    }
}