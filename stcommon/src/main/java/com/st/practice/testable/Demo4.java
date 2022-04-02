package com.st.practice.testable;

/**
 * @author: st
 * @date: 2022/3/19 00:31
 * @version: 1.0
 * @description:
 */
public class Demo4 {
  public static void main(String[] args) {

    String[] ss = new String[] {"a", "b"}; //
    for (String s : ss) {

		if(s.equals("a")){
        s = "cccc";
		}

      String x = s;
      System.out.println(s);
	}
  }
}
