package com.st.api.practice.jvm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author: st
 * @date: 2021/7/21 02:52
 * @version: 1.0
 * @description:
 */

@Slf4j
public class StackOver {

  private static int count;

@Test
  public void count(){
    count++;
    log.info("count:{}", count);
    //count();
  }
}
