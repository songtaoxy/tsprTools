package com.st.utils.collection;

import com.st.utils.log.LogUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class MapUtilTest {

  @Test
  void mergeMaps() {
    Map<String, String> map1 = new HashMap<String, String>();
    map1.put("1", "1");
    map1.put("2", "2");
    map1.put("3", "3");
    map1.put("4", "4");
    map1.put(null, "null1");

    Map<String, String> map2 = new HashMap<String, String>();
    map2.put("3", "33");
    map2.put("4", "44");
    map2.put("5", "5");
    map2.put("6", "6");
    map2.put(null, "null2");

    MapUtil.mergeMaps(map1, map2);

    //LogUtils.foal(MapUtil.mergeMaps(map1, map2), "");
  }

  @Test
  void putall() {
    MapUtil mapUtil = new MapUtil();
    mapUtil.putall();
    System.out.println("hi");
  }
}
