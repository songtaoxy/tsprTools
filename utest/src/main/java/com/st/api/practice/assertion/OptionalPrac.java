package com.st.api.practice.assertion;

import com.google.common.base.Preconditions;
import lombok.Data;

import java.util.Optional;

/**
 * @author: st
 * @date: 2021/6/11 01:17
 * @version: 1.0
 * @description:
 */

public class OptionalPrac {

	public static void add() {
		String res = null;
		Optional<String> res1 = Optional.ofNullable(res);
		// 如果是空, 返回另一个值
		String s = res1.orElse("xxx");
		System.out.println(s);

		Person person = new Person();
		person.setName("hi");
		Optional<Person> person11 = Optional.ofNullable(person);
		person11.ifPresent(v->{
			System.out.println(v.getName());
		});

		// 如果是空, 则抛出异常
		String s1 = Preconditions.checkNotNull("ok","err1");
		System.out.println(s1);
		Preconditions.checkNotNull(res, "err2");// 抛出异常, 如未被处理, 则程序中断
		Preconditions.checkNotNull(res, "errr3");

	}

	public static void main(String[] args) {
		add();
	}
}

@Data
class Person {
	private String name;

}




