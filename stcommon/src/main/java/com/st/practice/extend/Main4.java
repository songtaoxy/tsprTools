package com.st.practice.extend;

import java.util.Arrays;

/**
 * @author: st
 * @date: 2022/4/28 21:11
 * @version: 1.0
 * @description:
 */
public class Main4 {
	public static void main(String[] args) {
		int[] a = new int[] { 9, 20, 3, 16, 6, 5, 7, 1 };
		int i = new Main4().maximumDifference(a);
		System.out.println(i);
	}
	public int maximumDifference(int[] nums) {
		int res = -1;
		int n = nums.length;
		for(int i =0;i<n;++i) {
			for(int j=i+1;j<n;++j) {
				if(nums[j] > nums[i]) {
					res = Math.max(res,nums[j]-nums[i]);
				}
			}
		}
		return res;
	}
}
