package com.st.api.practice.regx;

import cn.hutool.core.util.StrUtil;

/**
 * @author: st
 * @date: 2023/1/17 17:16
 * @version: 1.0
 * @description:
 */
public class T2 {
	public static void main(String[] args) {
		String s1 = "${0[0]}_${0[1]}_${0[2]}_${0[3]}_json_${0[channel]}.png";
		String s2 = "1";
		//String s = s1.replaceAll("${0[0]}", s2);
		String s = StrUtil.replace(s1, "${0[0]}", s2);
		System.out.println(s);
	}

}
