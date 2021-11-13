package com.st.practice.mockito;

/**
 * 使用junit4+mockito+powerMockito 测试静态方法 <p>
 *
 * powerMockito 不支持junit5
 *
 * @author: st
 * @date: 2021/11/13 18:44
 * @version: 1.0
 * @description:
 */
public class MockitoStaicMethodWihtJunit4 {

  public static int add(int a, int b) {

    int c = a + b;
    return c;
  }
}
