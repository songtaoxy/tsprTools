package com.st.practice.enu;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通过 javap Edemo 可以知道该类的详情. idea反编译不行.
 */
@Getter
@AllArgsConstructor
public enum Edemo2 {

	RED( "red"), BLUE( "blue");
	//private final String name;
	private final String des;

	public static void main(String[] args) {

		System.out.println(Edemo2.RED.name());

	}
}
