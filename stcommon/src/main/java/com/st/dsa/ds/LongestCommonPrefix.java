package com.st.dsa.ds;

import ch.qos.logback.core.joran.conditional.ElseAction;

/**
 * @author: st
 * @date: 2022/3/17 00:10
 * @version: 1.0
 * @description:
 */
public class LongestCommonPrefix {
  public static void main(String[] args) {
    String[] s1 = {"flower", "flow", "flight"};
    String[] s2 = {"dog", "racecar", "car"};

    System.out.println(longestCommonPrefix(s1)); // fl
    System.out.println(longestCommonPrefix(s2)); // ""
  }

  public static String longestCommonPrefix(String[] strs) {

    // 定义上面长度的字符串数组用以存储前缀
    String ans = strs[0];

    for (String str : strs) {
      int j = 0;
      for (; j < ans.length() && j < str.length(); j++) {
        if (ans.charAt(j) != str.charAt(j)) break;
      }
      ans = ans.substring(0, j);
    }

    return ans;
  }
}
