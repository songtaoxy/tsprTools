package com.st.practice.stream;

import cn.hutool.core.util.RandomUtil;
import com.st.utils.string.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: st
 * @date: 2022/3/11 22:54
 * @version: 1.0
 * @description:
 */
public class StreamDemo {
  public static void main(String[] args) {
	  int[] ints = RandomUtil.randomInts(10);
	  //Arrays.stream(ints).forEach(System.out::println);


	  List<String> strings = Strings.randomStrings();
	  strings.stream().forEach(System.out::println);
  }//
  }
