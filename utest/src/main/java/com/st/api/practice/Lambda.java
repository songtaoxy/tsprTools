package com.st.api.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2021/11/11 20:43
 * @version: 1.0
 * @description:
 */
public class Lambda {

	@Test
	public void lambda(){

    String[] strs = {"hi", "no", "yes","yes2","yes3"};
    List<String> strList = new ArrayList<String>();

    strList = Arrays.asList(strs);

    strList.stream().map(str -> str + "_sufix").filter(s->s.contains("yes")).forEach(System.out::println);
	}
}
