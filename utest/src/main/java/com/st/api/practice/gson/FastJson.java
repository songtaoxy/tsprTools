package com.st.api.practice.gson;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import sun.util.resources.cldr.ig.LocaleNames_ig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/11/5 16:52
 * @version: 1.0
 * @description:
 */
@Slf4j
public class FastJson {

  @Test
  void prettyJson() {

    Map<String, String> map = new HashMap<String, String>();
    map.put("k1", "v1");
    map.put("k2", "v2");

    String s = JSON.toJSONString(map,true);

    log.info(s);

    log.info("\n======================================\n" +
               "The request param named with param is:\n" +
               "======================================\n" +
               "{}", s);

  }
}
