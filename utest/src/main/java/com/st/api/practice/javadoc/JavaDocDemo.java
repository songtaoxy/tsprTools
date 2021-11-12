package com.st.api.practice.javadoc;

import java.io.IOException;

/**
 * @author: st
 * @date: 2021/11/10 20:58
 * @version: 1.0
 * @description:
 */
public class JavaDocDemo {

  /**
   * search with {@link #JavaDocDemo} <p>
   * code demo <pre>{@code
   *              a
   *              b
   *              c}
   * search with {@link http://www.baidu.com}} <p>
   * search with {@link <a href="http://www.baidu.com">http://www.baidu.></a>} <p>
   * search with  <a href="http://www.baidu.com">http://www.baidu</a>
   * @
   * @param str
   */
  public JavaDocDemo(String str) {
    System.out.println(str);
  }


}
