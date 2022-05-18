package com.st.practice.variable;

import lombok.AllArgsConstructor;

/**
 * @author: st
 * @date: 2022/5/18 06:11
 * @version: 1.0
 * @description:
 */
@AllArgsConstructor
public class Variable {
	private String name;
	public static String name_static = "name_static";

	public String v_m1(String param) {

		System.out.println(param);

		return param + "appened";
	}

	public static void main(String[] args) {
		Variable variable = new Variable("name");
		variable.v_m1("param_local");

	}


}
