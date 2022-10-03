package com.st.practice.hutool;

import cn.hutool.core.util.StrUtil;

/**
 * @author: st
 * @date: 2022/10/3 16:52
 * @version: 1.0
 * @description:
 */
public class HutoolDemo {
	public static void main(String[] args) {

		strUtilT();
	}


	public static void strUtilT() {

		String s = "{} 很想测试这个工具, {}, And do you have?";
		String format = StrUtil.format(s, "I", "you");
		System.out.println(format);

	}
}
