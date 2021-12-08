package com.st.utils.collection;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/12/8 15:53
 * @version: 1.0
 * @description:
 */
@Slf4j
public class MapUtil {

  /**
   * 合并两个 {@link Map<String,String>}<p></p>
   *
   * 合并两个Map, 如果两个map有相同的key, 则后面的map将会覆盖前面的map
   *
   * <pre>
   *    // 如果key相同, 则map2的value会覆盖 map1的value
   *    result.putAll(map1);
   *    result.putAll(map2);
   * </pre>
   *
   * @param map1
   * @param map2
   * @return 合并后的map
   */
  public static Map<String, String> mergeMaps(Map<String, String> map1, Map<String, String> map2) {
    HashMap<String, String> result = Maps.newHashMap();
    result.putAll(map1);
    result.putAll(map2);

    return result;
  }
}
