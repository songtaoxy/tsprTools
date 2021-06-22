package com.st.api.practice.lambda;

import java.security.PrivilegedExceptionAction;

/**
 * @author: st
 * @date: 2021/6/22 11:21
 * @version: 1.0
 * @description:
 */
public class Con {

  private String name;

	public Con(String name, String age) {
		this.name = name;
		this.age = age;
	}

	private String age;

	public Con(String name) {
		this.name = name;
	}

	public int  myMethod(int param) {
    System.out.println("hi");
    return 0;
	}

  public static void main(String[] args) {
  }
}
