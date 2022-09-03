package com.st.practice.extend;

/**
 * @author: st
 * @date: 2022/4/20 17:22
 * @version: 1.0
 * @description:
 */
public class Son2 extends Parent {

	private String name;

	public Son2(String name) {
		super(name);
		this.name = name + "_this";
	}


	public String getParentName() {
		return super.getName();

	}

	public String getThisName() {
		return this.name;

	}
}
