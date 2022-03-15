package com.st.dsa;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author: st
 * @date: 2022/3/15 12:33
 * @version: 1.0
 * @description:
 */

/**
 *
 *
 * <pre>
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
 *
 * 你可以按任意顺序返回答案
 *
 * 来源：力扣（LeetCode）
 * 链接：<a href="https://leetcode-cn.com/problems/two-sum">two-sum</a>
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 *
 * 输入：nums = [2,7,11,15], target = 9
 * 输出：[0,1]
 * 解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
 *
 * </pre>
 */
public class SumTarget {
  public static void main(String[] args) {

    int[] array = {2, 7, 11, 15};
    int target = 9;

    int[] x = twoSum(array, target);
  }

  public static int[] twoSum(int[] nums, int target) {

    int[] res = new int[2];
    HashMap<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
      if (map.containsKey(target - nums[i])) {
        res[0] = i;
        res[1] = map.get(target - nums[i]);

        Arrays.stream(res).forEach(System.out::println);

        return res;
      }
      // 数组值为key，索引为value
      map.put(nums[i], i);
    }

    return res;
  }
}
