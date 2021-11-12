package com.st.api.practice.guava;

import com.google.common.base.Preconditions;
import com.st.utils.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author: st
 * @date: 2021/11/6 15:06
 * @version: 1.0
 * @description:
 */
@Slf4j
public class GuavaTest {

  @Test
  void testGuava() {

    String param = "未读代码";
    param = null;
    String name = Preconditions.checkNotNull(param);
    //String name = Preconditions.checkNotNull(param,"param can't be null");

    LogUtils.formatObjAndLogging(name,"name is or not null");
  }
}
