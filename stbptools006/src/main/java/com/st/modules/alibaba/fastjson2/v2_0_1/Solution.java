package com.st.modules.alibaba.fastjson2.v2_0_1;

/**
 * @author: st
 * @date: 2022/4/29 17:35
 * @version: 1.0
 * @description:
 */
public class Solution {

	public boolean isPalindrome(String s) {
		int left = 0, right = s.length() - 1;
		while (left < right) {
			while (left < right && !Character.isLetterOrDigit(s.charAt(left)))
				left++;
			while (left < right && !Character.isLetterOrDigit(s.charAt(right)))
				right--;
			if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right)))
				return false;
			left++;
			right--;
		}
		return true;
	}

	public static void main(String[] args) {
		String s = "A man, a plan, a canal: Panama";
		Solution solution = new Solution();
		boolean palindrome = solution.isPalindrome(s);

		System.out.println(palindrome);

	}
}
