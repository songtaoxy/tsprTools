package com.st.practice.logback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class LogbackDemoTest {

  @Test
  void logbackLevel() {
    new LogbackDemo().logbackDem();




    log.info("hi");
  }


}

