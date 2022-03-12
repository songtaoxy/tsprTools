package com.st.utils.middle;

/**
 * @author: st
 * @date: 2022/3/13 02:12
 * @version: 1.0
 * @description:
 */
public class Middle {

  public static void main(String[] args) {
    System.out.println(middle(1, 2)); // 0
    System.out.println(middle(0, 1)); // 1
    System.out.println(middle(3, 5)); // 4
  }

  /**
   * <pre>
   * 给起始值, 计算中间值
   *
   * 应用: 给定array[]起始点索引, 得到中间索引
   * a[i], a[j] -> a[(i+j)/2]
   * 如二分查找法, 归并排序等, 都经常会用到这些.
   *
   * </pre>
   *
   * @param start
   * @param end
   * @return
   */
  public static int middle(int start, int end) {

    if (start <= end) {

      // 除以2, 等效的位移: 右移动1位
      // return (start + end) / 2;
      return start + ((end - start) >> 1);
    }

    return -1;
  }
}
