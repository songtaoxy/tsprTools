package com.st.utils.common;


import java.util.HashMap;
import java.util.Map;

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

    //LogUtils.foal(map,"");

    return map;
  }
}
