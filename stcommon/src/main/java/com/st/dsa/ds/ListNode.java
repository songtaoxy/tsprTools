package com.st.dsa.ds;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * <pre>
 *  Copyright (C), 2015-2018
 *  FileName: ListNode
 *  Author:   st
 *  Date:     2022.03.15
 *  Description: 链表用到的数据结构
 *  History:
 *  <author>          <time>          <version>          <desc>
 *  </pre>
 */
public class ListNode {
  public int val;
  public ListNode next;

  public ListNode(int x) {
    val = x;
    next = null;
  }

  public List<Integer> toArray() {
    List<Integer> list = new ArrayList<>();

    ListNode curNode = this;

    do {
      list.add(curNode.val);
      curNode = curNode.next;
    } while (curNode != null);

    return list;
  }
}
