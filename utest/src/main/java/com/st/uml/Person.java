package com.st.uml;

public interface Person {

static 	String name = null;


	default void setName(String name){
    System.out.println("my name is tt");
	};


}
