package com.st.yonyou;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: st
 * @date: 2021/7/28 15:41
 * @version: 1.0
 * @description:
 */
@Slf4j
public class TestDateSql {

  @Test
  public void mysqlTime2StardandDate() {





    // ############################################
    // # 获取的是时间戳是 String 类型
    // ############################################
    // String sTime=item.getUpdateTime()+"000";  //把0换回去
    // 2005-04-06 09:01:10
    // 1112749270000
    String sTime = "1112749270"+"000";
    //String sTime = "-220867200"+"000";

    long lTime = Long.parseLong(sTime); // int放不下的，用long


    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    String sLastTime = sdf.format(lTime);

    log.info(sLastTime);
    // log.info("1970"+(Long.parseLong(sLastTime)-1970)/1000);




    // ############################################
    // # 获取的是时间戳是 int类型
    // ############################################
    int sqlTimestamp = 1562397569;
    Date date = new Date(1000l * sqlTimestamp);
    log.info(sdf.format(date));
  }


  @Test
  public void mysqlTime2StardandDate_2() throws ParseException {
    java.util.Date today = new java.util.Date();



    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date format = sdf.parse("2005-04-06 09:01:10");
    log.info(String.valueOf(format.getTime()));
    long todayTime = today.getTime();
    log.info(String.valueOf(todayTime));

    java.sql.Timestamp ts1 = new java.sql.Timestamp(today.getTime());
    java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2005-04-06 09:01:10");

    long tsTime1 = ts1.getTime();
    long tsTime2 = ts2.getTime();

    System.out.println("today-sql :"+tsTime1);
    System.out.println("today-java:"+String.valueOf(todayTime));
    System.out.println(tsTime2);
  }
}
