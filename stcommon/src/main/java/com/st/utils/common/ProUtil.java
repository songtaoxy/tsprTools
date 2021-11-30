package com.st.utils.common;

import com.st.utils.log.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2021/11/30 08:23
 * @version: 1.0
 * @description:
 */
public class ProUtil {

  public Map pro2map() {

    Map map = new HashMap<>();

    map = System.getProperties();

    map.entrySet().stream().forEach(System.out::println);

    LogUtils.formatObjAndLogging(map,"");

    return map;
  }
}
