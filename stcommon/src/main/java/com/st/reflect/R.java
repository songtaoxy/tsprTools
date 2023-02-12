package com.st.reflect;

import com.st.utils.common.ProUtil;

/**
 * @author: st
 * @date: 2023/2/8 10:13
 * @version: 1.0
 * @description:
 */
public class R {

	public static void main(String[] args) {

	}

	public void test(String[] args) {

		//  编译期异常, 必须处理
		try {
			Class<?> aClass = Class.forName("ch.qos.logback.core.util.TimeUtil");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}


		// 需要import对应的包,see 上面的:
		// import com.st.utils.common.ProUtil;
		Class<ProUtil> proUtilClass = ProUtil.class;


		Class<? extends R> aClass =this.getClass();


		//  编译期异常, 必须处理
		try {
			Class<?> aClass1 = aClass.getClassLoader().loadClass("com.st.utils.common.BasicUtils");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
