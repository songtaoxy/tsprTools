package com.st.practice.logback;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: st
 * @date: 2021/11/22 10:55
 * @version: 1.0
 * @description:
 */
@Slf4j
public class  LogbackDemo {

  public static void main(String[] args) {
    log.info("hi");
    log.debug("hi");
    log.warn("hi");
    log.error("hi");

    log.info(System.getProperty("jdk.modules.path"));

   /* while (true) {
      log.info("...");
    }*/
  }

  public void logbackDem(){
    log.info("logbackDemo");
  }
}
