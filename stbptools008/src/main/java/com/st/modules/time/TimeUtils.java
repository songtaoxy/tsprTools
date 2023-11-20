package com.st.modules.time;

import org.assertj.core.internal.bytebuddy.asm.Advice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: st
 * @date: 2021/11/11 19:23
 * @version: 1.0
 * @description:
 */
public class TimeUtils {

    private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
      @Override
      public SimpleDateFormat initialValue() {
        return new SimpleDateFormat("yyyy-MM-dd");
      }
    };


  public static String format(Date date) {
    return formatter.get().format(date);
  }

  public static Date parse(String date) {
    try {
      return formatter.get().parse(date);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 获取当前系统时区的时间 <p>
   *
   * 获取当前系统时区的时间. 格式为: 2021-11-11T19:22:42.228
   *
   * @return 获取当前系统时区的时间: 2021-11-11T19:22:42.228
   */
 /* public static LocalDateTime getLocalDateTime() {

    Instant instant = new Date().toInstant();
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
    //System.out.println(localDateTime);

    return localDateTime;
  }*/


  /**
   * @deprecated since 2021.11.11 by st; replace with
   * @return
   */
  public static long costTimeMillsOld(){
    long start = System.currentTimeMillis();

    // 业务处理, 如执行sql等
    System.out.println("...");


    long end = System.currentTimeMillis();

    long cost = end - start;

    System.out.println(cost);

    return cost;
  }


  public static long costTimeMills(long start, long end){

    long cost = end - start;
    //System.out.println(cost);

    return cost;
  }


}
