package com.st.dsa.sort;

public class QuickSort {


    /**
     * 快速排序/左右双指针;
     * 参数说明:
     *     a -- 待排序的数组
     *     l -- left  数组的左边界(例如，从起始位置开始排序，则l=0)
     *     r -- right 数组的右边界(例如，排序截至到数组末尾，则r=a.length-1)
     *     povit -- 基准
     */
    public static void sort(int[] a, int l, int r) {

        if (l < r) {
            int i,j,povit;

            i = l;
            j = r;
            povit = a[i]; // 基准值

            while (i < j) {

                while(i < j && a[j] > povit)
                    j--; // 从右向左找第一个小于x的数
                if(i < j){
                    a[i] = a[j];
                    i++;
                }

                while(i < j && a[i] < povit)
                    i++; // 从左向右找第一个大于x的数
                if(i < j){
                    a[j] = a[i];
                    j--;
                }
            }

            a[i] = povit;
            print(a);
            System.out.print("l=" + (l + 1) + "h=" + (r + 1) + "povit=" + povit + "\n");

            sort(a, l, i-1); /* 递归调用 */
            sort(a, i+1, r); /* 递归调用 */
        }
    }



        static void print(int[] arr) {
            for (int i = 0; i < arr.length; i++) {
                System.out.print(arr[i] + " -> ");
            }
            System.out.println();
        }

        public static void main(String[] args) {
            int low = 0;
            int high = 18;
            int[] arr = { 45, 43, 16, 4, 36, 36, 12, 17, 43, 12, 42, 7, 26, 23, 35, 4, 14, 21, 9 };
            QuickSort.sort(arr, low, high);
        }
    }


