package com.st.utils.common;

import com.st.utils.log.LogUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TimeUtilsTest {

  @Test
  void getLocalDateTime() {

    LocalDateTime localDateTime = TimeUtils.getLocalDateTime();
    //System.out.println(localDateTime);

    LogUtils.foal(localDateTime,"当前时区时间");
  }


  @Test
  void costTimeMillsOld() {

    Long costTimeMills = TimeUtils.costTimeMillsOld();

    LogUtils.foal(costTimeMills,"耗时(毫秒)");

  }

  @Test
  void costTimeMills() throws InterruptedException {
    Long start = System.currentTimeMillis();
    Thread.sleep(10);
    Long  end = System.currentTimeMillis();
    Long costTimeMills = TimeUtils.costTimeMills(start, end);
    LogUtils.foal(costTimeMills+" mills","当前耗时(毫秒)");

  }
}
