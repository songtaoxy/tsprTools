package com.st.modules.time;

import javax.xml.stream.events.EndElement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Stack;

/**
 * @author: st
 * @date: 2021/11/11 19:23
 * @version: 1.0
 * @description:
 */
public class TimeUtils {

  /**
   * 获取当前系统时区的时间 <p>
   *
   * 获取当前系统时区的时间. 格式为: 2021-11-11T19:22:42.228
   *
   * @return 获取当前系统时区的时间: 2021-11-11T19:22:42.228
   */
  public static LocalDateTime getLocalDateTime() {

    Instant instant = new Date().toInstant();
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
    //System.out.println(localDateTime);

    return localDateTime;
  }


  /**
   * @deprecated since 2021.11.11 by st; replace with
   * @return
   */
  public static Long costTimeMillsOld(){
    Long start = System.currentTimeMillis();

    // 业务处理, 如执行sql等
    System.out.println("");

    Long end = System.currentTimeMillis();

    Long cost = end - start;

    System.out.println(cost);

    return cost;
  }


  public static Long costTimeMills(Long start, Long end){

    Long cost = end - start;
    //System.out.println(cost);

    return cost;
  }


}
