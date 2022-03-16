package com.st.dsa;

import ch.qos.logback.core.net.SyslogOutputStream;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2022/3/16 00:46
 * @version: 1.0
 * @description:
 */
public class LengthOfLongestSubstring {

  public static int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> map = new HashMap<>();
    int ans = 0, n = s.length();
    for (int i = 0, j = 0; j < n; j++) {
      if (map.containsKey(s.charAt(j))) {
        // 注意! 此处map.get(s.charAt(j))获得的值必须与i进行判断,
        // 如果不进行判断,则可能出现i索引后退的情况,如参数字符串为abba时.
        i = Math.max(i, map.get(s.charAt(j)));
      }
      ans = Math.max(ans, j - i + 1);
      map.put(s.charAt(j), j + 1);
    }
    return ans;
  }

  public static int lengthOfLongestSubstring2(String s) {
    int n = s.length(), ans = 0;
    int[] index = new int[128];
    for (int j = 0, i = 0; j < n; j++) {
      //更新i的值
      i = Math.max(index[s.charAt(j)], i);
      ans = Math.max(ans, j - i + 1);
      // 向保存字符的数组中赋值
      index[s.charAt(j)] = j + 1;
    }
    return ans;
  }

  public static void mathTest(){
    int i = 1 / 0;
  }

  public static void main(String[] args) throws Exception{
    String s =  "abcabcbb";
    s = "abcccbba";
    int i = lengthOfLongestSubstring(s);
    //int i = lengthOfLongestSubstring2(s);
    System.out.println(i);

    try{

      mathTest();
    }catch (Exception e){
      System.out.println(e.getMessage());
      //System.out.println(e.getStackTrace());
    }
  }

}
