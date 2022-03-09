package com.st.utils.cost;

import lombok.extern.slf4j.Slf4j;

/** Some service cost time in mills, and logged. */

/**
 * @author: st
 * @date: 2022/3/9 19:33
 * @version: 1.0
 * @description:
 */
@Slf4j
public class CostUtils {
  public static void main(String[] args) throws InterruptedException {
    //
    Long start = start();
    Thread.sleep(2);

    log.info(costs(start, "Copy dirs and files"));
    log.info(costs(start, ""));
    log.info(costs(start, null));
  }

  public static Long start() {
    return System.currentTimeMillis();
  }

  public static Long end() {
    return System.currentTimeMillis();
  }

  public static Long costs(Long start) {
    return System.currentTimeMillis() - start;
  }

  public static String costs(Long start, String tips) {

    long cost = System.currentTimeMillis() - start;

    if (null == tips || tips.isEmpty()) {
      tips = "The current service";
    }

    // return tips + " cost [" + cost + "] millis.";
    return "[" + cost + "] millis cost for [" + tips + "].";
  }
}
