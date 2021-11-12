package com.st.utils.log;

import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.runners.Parameterized;

class LogUtilsTest {

  @Test
  void formatObjAndLogging(String strings) {

    String o = "hi";
    String x = "";
    // x = null;
    // x = "this is my ps";
    LogUtils.formatObjAndLogging(o, x);


  }
}
