package com.st.practice.enu;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通过 javap Edemo 可以知道该类的详情. idea反编译不行.
 */
@Getter
@AllArgsConstructor
public enum Edemo {

	RED("r", "red"), BLUE("b", "blue");
	private final String name;
	private final String des;

	public static void main(String[] args) {

		System.out.println(Edemo.RED.getName());
		System.out.println(Edemo.RED.name());

	}
}
