package com.st.dsa;

/**
 * @author: st
 * @date: 2022/3/15 20:22
 * @version: 1.0
 * @description:
 */
import com.st.dsa.ds.ListNode;
import org.junit.Assert;

/**
 *
 *
 * <pre>
 * <a href="https://leetcode-cn.com/problems/add-two-numbers">两数相加</a>
 *
 * 给你两个非空的链表，表示两个非负的整数。
 * 它们每位数字都是按照逆序的方式存储的，
 * 并且每个节点只能存储一位数字。
 *
 * 如一个3位的数: 387(三百八十七)
 * 顺序: 个位-十位-百位 3-8-7
 * 逆序: 百位-十位-个位 7-8-3
 *
 * 请你将两个数相加，并以相同形式返回一个表示和的链表。
 *
 * 你可以假设除了数字 0 之外，这两个数都不会以0开头
 *
 * 输入：l1 = [2,4,3], l2 = [5,6,4]
 * 输出：[7,0,8]
 * 解释：342 + 465 = 807.
 * </pre>
 */
public class AddTwoNumbers {

  public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode resultList = new ListNode(0);
    // 进位. 要么是0, 要么是1(逢十进一)
    int cache = 0;

    ListNode l3 = resultList;
    while (l1 != null || l2 != null || cache > 0) {
      int l1Val = l1 == null ? 0 : l1.val;
      int l2Val = l2 == null ? 0 : l2.val;
      int l3Val = l1Val + l2Val + cache;
      cache = 0;

      // 判断是否大于 9 大于9 进一位
      if (l3Val > 9) {
        cache = 1;
        l3Val = l3Val - 10;
      }

      l3.next = new ListNode(l3Val);

      l3 = l3.next;
      l1 = l1 == null ? l1 : l1.next;
      l2 = l2 == null ? l2 : l2.next;
    }

    return resultList.next;
  }

  public static void test002() {
    // 创建测试案例
    ListNode listNode1 = new ListNode(5);
    ListNode listNode2 = new ListNode(6);
    ListNode listNode3 = new ListNode(4);
    listNode1.next = listNode2;
    listNode2.next = listNode3;

    ListNode listNode21 = new ListNode(2);
    ListNode listNode22 = new ListNode(4);
    ListNode listNode23 = new ListNode(3);
    listNode21.next = listNode22;
    listNode22.next = listNode23;

    ListNode listNode31 = new ListNode(1);
    ListNode listNode32 = new ListNode(1);

    // 测试案例期望值
    Integer[] expResult1 = new Integer[] {7, 0, 8};
    Integer[] expResult3 = new Integer[] {2};

    // 执行方法
    AddTwoNumbers solution2 = new AddTwoNumbers();
    ListNode result1 = solution2.addTwoNumbers(listNode1, listNode21);
    //ListNode result3 = solution2.addTwoNumbers(listNode31, listNode32);
    // 判断期望值与实际值
    Assert.assertArrayEquals(expResult1, result1.toArray().toArray());
    //Assert.assertArrayEquals(expResult3, result3.toArray().toArray());
  }

  public static void main(String[] args) {
    test002();
  }
}
