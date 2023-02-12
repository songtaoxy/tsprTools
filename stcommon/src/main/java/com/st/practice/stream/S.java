package com.st.practice.stream;

import cn.hutool.core.util.ObjectUtil;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: st
 * @date: 2023/2/7 10:04
 * @version: 1.0
 * @description:
 */
public class S {
	public static void main(String[] args) {
		List arrayList = null;
		//arrayList.stream().forEach(System.out::println);

		Integer a = 10;
		Integer b = 10;
		Integer c = 10;
		Integer d = 11;


		ArrayList<Integer> integers = new ArrayList<>();
		integers.add(a);
		integers.add(b);

		System.out.println(integers.contains(c));
		System.out.println(integers.contains(d));

		System.out.println(ObjectUtil.equals(a,b));


	}
}
