package com.st.dsa.offer;

/**
 * @author: st
 * @date: 2023/2/12 08:49
 * @version: 1.0
 * @description:
 */
public class Offer_0003_数组中重复的数字 {
	public static int findRepeatNumber(int[] nums){
		if(nums.length==0){
			return -1;
		}
		for (int i = 0; i < nums.length; i++) {
			if (nums[i]<0 || nums[i]>nums.length-1) {
				return -1;
			}
		}
		for (int i = 0; i < nums.length; i++) {
			while(nums[i]!=i){
				if(nums[i]==nums[nums[i]]){
					return nums[i];
				}
				int tmp = nums[i];
				nums[i]=nums[tmp];
				nums[tmp]=tmp;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		int[] array=new int[]{2,3,1,0,2,5,3};
		System.out.println(findRepeatNumber(array));

	}
}
