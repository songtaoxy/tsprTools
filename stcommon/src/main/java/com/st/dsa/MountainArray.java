package com.st.dsa;

import com.google.common.collect.PeekingIterator;

/**
 * @author: st
 * @date: 2022/3/13 01:03
 * @version: 1.0
 * @description:
 */
public class MountainArray {

  public static void main(String[] args) {
    int[] ints = new int[] {5, 6, 7, 8, 9, 5, 4, 3, 3, 2};

    System.out.println(peakIndexInMountainArray(ints));
  }

	public static int peakIndexInMountainArray(int[] arr) {
		int n = arr.length;
		int left = 1, right = n - 2, ans = 0;
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

}
