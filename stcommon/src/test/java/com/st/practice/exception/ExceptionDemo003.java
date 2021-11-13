package com.st.practice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 异常之后, 代码能否继续执行
 *
 * <ul>
 *   <li> 只有try catch, 且catch中, 没有throw, 才能继续执行
 * </ul>
 *
 * @author: st
 * @date: 2021/11/13 14:47
 * @version: 1.0
 * @description:
 */
public class ExceptionDemo003 {

  /**
   * 未捕获异常, 异常之后的代码, 不能继续执行
   *
   * @param div
   */
  @DisplayName("异常之后的代码能否继续执行")
  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 0})
  void em001(int div) {

    System.out.println(100 / div);

    System.out.println("异常之后的代码能否继续执行?");
  }

  /**
   * 捕获异常, 异常之后的代码, 能继续执行
   *
   * @param div
   */
  @DisplayName("异常之后的代码能否继续执行")
  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 0})
  void em002(int div) {

    try {
      System.out.println(100 / div);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("异常之后的代码能否继续执行?");
  }

  /**
   * 捕获异常, 异常之后的代码, 不能继续执行
   *
   * <p>throw 之后, 不能执行
   *
   * @param div
   */
  @DisplayName("异常捕获与否的影响:捕获")
  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 0})
  void em003(int div) {

    try {
      System.out.println(100 / div);
    } catch (Exception e) {
      throw e;
    }

    System.out.println("异常之后的代码能否继续执行?");
  }

  /**
   * 捕获异常, 异常之后的代码, 不能继续执行
   *
   * <p>throw 之后, 不能执行
   *
   * @param div
   */
  @DisplayName("异常捕获与否的影响:捕获")
  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 0})
  void em004(int div) throws Exception {

    try {
      System.out.println(100 / div);
    } catch (Exception e) {
      throw new Exception(e);
    }

    System.out.println("异常之后的代码能否继续执行?");
  }

  /**
   * 仅仅throws, 不能执行
   *
   * @param div
   * @throws Exception
   */
  @DisplayName("异常捕获与否的影响:捕获-throws")
  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 0})
  void em005(int div) throws Exception {

    System.out.println(100 / div);

    System.out.println("continue");
  }
}
