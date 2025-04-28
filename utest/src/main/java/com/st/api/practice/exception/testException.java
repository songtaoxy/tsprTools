package com.st.api.practice.exception;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author: st
 * @date: 2021/11/9 10:12
 * @version: 1.0
 * @description:
 */
@Slf4j
public class testException {

  @Test
  void testException() {
    try {

      System.out.println(1 / 0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //LogUtils.foal("obj", "messagee");
  }
}
