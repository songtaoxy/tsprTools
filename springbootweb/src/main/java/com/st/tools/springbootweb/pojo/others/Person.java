package com.st.tools.springbootweb.pojo.others;

/**
 * @author: st
 * @date: 2021/12/31 15:05
 * @version: 1.0
 * @description:
 */
public class Person <T> {

		// 使用T类型定义变量
		private T info;

		// 使用T类型定义一般方法
		public T getInfo() {

			return info;
		}

		public void setInfo(T info) {

			this.info = info;
		} // 使用T类型定义构造器

		public Person() {}

		public Person(T info) {

			this.info = info;
		}
	}
