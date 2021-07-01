package com.st.api.practice.pojo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author: st
 * @date: 2021/6/29 19:18
 * @version: 1.0
 * @description:
 */
@Data
public class Person {

	String name;
	LocalDate birthday;

	public Person(String name, LocalDate birthday) {
		this.name = name;
		this.birthday = birthday;
	}


	public LocalDate getBirthday() {
		return birthday;
	}


	public static int compareByAge(Person a, Person b) {
		return a.birthday.compareTo(b.birthday);
	}

	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", birthday=" + birthday +
				'}';
	}
}
