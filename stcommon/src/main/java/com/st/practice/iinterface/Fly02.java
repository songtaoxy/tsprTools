package com.st.practice.iinterface;

public interface Fly02 {
	void fly();


	default void eat() {
		System.out.println("inteface default eat() eating ....");
	}
}
