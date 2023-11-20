package com.st.modules.time;

import java.util.Date;

/**
 * @author: st
 * @date: 2023/11/20 16:20
 * @version: 1.0
 * @description:
 */
public class Main {
	public static void main(String[] args) {
		String format = TimeUtils.format(new Date());
		System.out.println(format);

		Date parse = TimeUtils.parse("2023-11-20");
		System.out.println(parse.getTime());
	}
}
