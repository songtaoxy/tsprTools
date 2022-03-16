package com.st.dsa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 *
 * <pre>
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
 *
 * 示例1:
 * 输入: s = "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 *
 * 链接：<a href="https://leetcode-cn.com/problems/longest-substring-without-repeating-characters">力扣:无重复字符的最长子串</a>
 *
 * see: <a href="https://juejin.cn/post/6844903896175869959">力扣:解法比较与说明</a>
 * </pre>
 *
 * @author: st
 * @date: 2022/3/16 00:46
 * @version: 1.0
 * @description:
 */
public class LengthOfLongestSubstring {

  /**
   *
   * 方法二: 基于窗口滑动的优化
   * @param s
   * @return
   */
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

  /**
   *
   *
   * <pre>
   * 方法三: 替换方法二中的HashMap为整数数组
   *
   * 对方法二的优化
   *
   * 当我们知道该字符集比较小的时侯，我们可以用一个整数数组作为直接访问表来替换 Map。
   * 解决了方法二种中以空间换时间的尴尬
   * </pre>
   *
   * @param s
   * @return
   */
  public static int lengthOfLongestSubstring2(String s) {
    int n = s.length(), ans = 0;
    int[] index = new int[128];
    for (int j = 0, i = 0; j < n; j++) {
      // 更新i的值
      i = Math.max(index[s.charAt(j)], i);
      ans = Math.max(ans, j - i + 1);
      // 向保存字符的数组中赋值
      index[s.charAt(j)] = j + 1;
    }
    return ans;
  }

  /**
   *
   *
   * <pre>
   *
   * 方法一: 原始 窗口滑动
   *
   * 我们可以使用 HashSet 将字符存储在当前窗口  [i,j) 中。 然后我们向右侧滑动索引 j，如果它不在 HashSet 中，我们会继续滑动 ,如果直s[j] 已经存在于 HashSet 中, 我们需要逐个将HashSet中的值remove , 直到将j对应的重复值移出Set,与此同时,索引i右移。
   * 这样做的目的是使所有可能存在的没有重复字符的最长子串都以索引i开头,如果我们对所有的i都这样做,就可以得到最后答案
   *
   *
   * 我们可以使用 HashSet 将字符存储在当前窗口  [i,j) 中。 然后我们向右侧滑动索引 j，如果它不在 HashSet 中，我们会继续滑动 ,如果直s[j] 已经存在于 HashSet 中, 我们需要逐个将HashSet中的值remove , 直到将j对应的重复值移出Set,与此同时,索引i右移。
   * 这样做的目的是使所有可能存在的没有重复字符的最长子串都以索引i开头,如果我们对所有的i都这样做,就可以得到最后答案
   *
   * </pre>
   *
   * @param s
   * @return
   */
  public int lengthOfLongestSubstringOrg(String s) {
    Set<Character> charSet = new HashSet<>();
    int i = 0,j = 0,ans = 0,n = s.length();
    while(i<n && j<n){
      if(!charSet.contains(s.charAt(j))){
        charSet.add(s.charAt(j++));
        ans = Math.max(ans,j-i);
      }else{
        charSet.remove(s.charAt(i++));
      }
    }
    return ans;
  }


  public static void main(String[] args) throws Exception {
    String s = "abcabcbb";
    s = "abcccbba";
    int i = lengthOfLongestSubstring(s);
    // int i = lengthOfLongestSubstring2(s);
    System.out.println(i);
  }
}
