package com.st.practice.stream;

import cn.hutool.core.util.RandomUtil;
import com.st.utils.string.StringUtils;
import com.st.utils.string.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2022/3/11 22:54
 * @version: 1.0
 * @description:
 */
public class StreamDemo {
  public static void main(String[] args) {

	  int[] ints = RandomUtil.randomInts(10);
	  Arrays.stream(ints).forEach(System.out::println);


	  List<String> strings = Strings.randomStrings();

	  List<String> collect = strings.stream().filter(s -> s.startsWith("1")).filter(s -> s.startsWith("2")).map(s->s+"hi").collect(Collectors.toList());

	  Optional<String> optionalS = Optional.ofNullable(null);
	  String s1 = optionalS.filter(s -> s.startsWith("x")).filter(s -> s.startsWith("b")).orElse("n");
	  System.out.println(s1);


	  System.out.println(collect.size());

	  //strings.stream().forEach(System.out::println);
  }
  }
