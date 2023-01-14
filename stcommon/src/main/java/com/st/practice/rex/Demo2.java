package com.st.practice.rex;

import cn.hutool.core.bean.BeanUtil;

/**
 * @author: st
 * @date: 2023/1/13 16:16
 * @version: 1.0
 * @description:
 */
public class Demo2 {
	public static void main(String[] args) {
		//String ip = "172.18.46.220,172.18.46.221";
		String ip = "172.18.46.220";
		String[] split = ip.split(",");
		int length = split.length;
		if (length > 1) {

			for (String ipStr : split) {
				System.out.println(ipStr);
			}

		}
	}
}
