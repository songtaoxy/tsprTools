package com.st.utils.log;

import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;

class LogUtilsTest {

  @Test
  void formatObjAndLogging() {

    String o = "hi";
    String x = "";
    // x = null;
    // x = "this is my ps";
    //LogUtils.formatObjAndLogging(o, x);
    LogUtils.formatObjAndLogging(o,"[元数据报表服务]-[CustomReportApiController#query]-[入参]-[param : Map<String,Object>]");

  }
}
