package com.st.dsa;

import com.google.common.collect.PeekingIterator;
import com.google.common.primitives.Ints;

import java.util.Arrays;

/**
 * @author: st
 * @date: 2022/3/13 01:03
 * @version: 1.0
 * @description:
 */
public class MountainArray {

  public static void main(String[] args) {
    int[] ints = new int[] {1, 5, 6, 7, 8, 9, 10, 5, 4, 3, 3, 2};

    System.out.println(peakIndexInMountainArray(ints));//6
    System.out.println(withMax(ints));//6
  }

  public static int peakIndexInMountainArray(int[] arr) {
    int left = 0, right = arr.length - 1, ans = 0;
    while (left <= right) {
      int mid = (left + right) / 2;
      if (arr[mid] > arr[mid + 1]) {
        ans = mid;
        right = mid - 1;
      } else {
        left = mid + 1;
      }
    }
    return ans;
  }

  public static int withMax(int[] array) {

    int asInt = Arrays.stream(array).max().getAsInt();
    int index = Ints.indexOf(array, asInt);
    return index;
  }
}

