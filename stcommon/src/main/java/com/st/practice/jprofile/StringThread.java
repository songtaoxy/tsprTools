package com.st.practice.jprofile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author: st
 * @date: 2022/2/25 16:27
 * @version: 1.0
 * @description:
 */
public class StringThread {
  private static String str = "1";

  public static void main(String[] args) throws Exception {

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    IntStream.range(0, 1000)
        .forEach(
            (i) ->
                executorService.submit(
                    () -> {
                      append("1");
                    }));
    executorService.awaitTermination(1, TimeUnit.SECONDS);
    System.out.println(str.length());
    executorService.shutdown();
  }

  private static synchronized void append(String s) {
    str = str + s;
  }
}
