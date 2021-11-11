package com.st.api.practice.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author: st
 * @date: 2021/8/23 16:41
 * @version: 1.0
 * @description:
 */
@Slf4j
public class ExceptionTrace {

  public static void main(String[] args) {

    try {
      m3();

    } catch (Exception e) {
      log.error("excepion-main===> ", e);
      // e.printStackTrace();
      // throw e;
    }
  }

  public static void m3() throws Exception {
    try {
      m2();
    } catch (Exception e) {
      log.error("excepion-m3===> ", e);
       //e.printStackTrace();
       throw e;
    }
  }

  public static void m2() throws Exception {
    m1();
  }

  public static void m1() throws Exception {

    try {
      System.out.println(1 / 0);
    } catch (Exception e) {
      //log.error("excepion-m1===> ", e);
      // e.printStackTrace();
      throw e;
    }
  }
}
