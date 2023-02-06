package com.st.practice.boo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: st
 * @date: 2023/2/2 10:26
 * @version: 1.0
 * @description:
 */
@Slf4j
public class Demo {
	public static void main(String[] args) {
		String x = "2;3;4";
		//String x = "2";
		String[] split = x.split(";");
		Arrays.stream(split).forEach(System.out::println);


		ArrayList<String> strings = new ArrayList<>();
		strings.add("111111");
		strings.add("111112");
		strings.add("211112");

		log.info(String.valueOf(strings.contains("111r3111")));


		AtomicInteger atomicInteger = new AtomicInteger();
		System.out.println(atomicInteger.get());
		System.out.println(atomicInteger.incrementAndGet());




	}
}
