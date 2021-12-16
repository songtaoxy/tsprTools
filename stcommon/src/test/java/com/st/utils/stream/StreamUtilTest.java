package com.st.utils.stream;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.st.utils.log.LogUtils;
import com.st.utils.props2json.PropsToJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

class StreamUtilTest {

  @Test
  void jsonStr2map() {

    String str = "{\"k1\":\"v1\", \"ak2\":\"v2\",\"k3\":\"v3\"}";

    Map<String, Object> map = StreamUtil.jsonStr2map(str);

    System.out.println(map);

    Map<String, Object> map1 =
        map.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("k"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Map<String, Object> map2 =
        map.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("k"))
            .collect(Collectors.toMap(p -> p.getKey().substring(1), Map.Entry::getValue));

    Map<String, Object> map3 =
        map.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("k"))
            .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue() + "---"));

    LogUtils.formatObjAndLogging(map1, "map1");
    LogUtils.formatObjAndLogging(map2, "map2");
    LogUtils.formatObjAndLogging(map3, "map3");
  }

  @Test
  void properties_map() {
    Properties properties = System.getProperties();
    // HashMap<String, String> map = properties;
    // properties.forEach(System.out::println);

    String s = PropsToJsonUtil.convertToJson(properties);

    JSONObject jsonObject = JSONObject.parseObject(s);

    Map<String, Object> map = (Map<String, Object>) jsonObject;

    LogUtils.formatObjAndLogging(map, "map");

    Map<String, Object> map1 =
        map.entrySet().stream()
            .filter(e -> e.getKey().startsWith("java"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Map<String, Object> map2 =
        map.entrySet().stream()
            .filter(e -> e.getKey().startsWith("java"))
            .collect(
                Collectors.toMap(p -> p.getKey().substring("java".length()), Map.Entry::getValue));

    LogUtils.formatObjAndLogging(map1, "map1");
    LogUtils.formatObjAndLogging(map2, "map2");

    /* Map map4 = properties;
    HashMap<String, String> map5 = (HashMap<String, String>) map4;
    Map<String, Object> map6 = map5.entrySet().stream()
            .filter(e -> e.getKey().startsWith("java"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    LogUtils.formatObjAndLogging(map6,"map6");*/

    HashMap<String, String> map3 = Maps.newHashMap(Maps.fromProperties(properties));
    map3.put("java.xxxx", "..............................");
    Map<String, Object> map4 =
        map3.entrySet().stream()
            .filter(e -> e.getKey().startsWith("java"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    LogUtils.formatObjAndLogging(map4, "map4");

    Map<String, Object> map5 =
        map3.entrySet().stream()
            .filter(e -> e.getKey().startsWith("java"))
            .collect(
                Collectors.toMap(p -> p.getKey().substring("java.".length()), Map.Entry::getValue));
    LogUtils.formatObjAndLogging(map5, "map5");
  }

  public static Map<String, Object> parseMapForFilterByOptional(Map<String, Object> map) {

    return Optional.ofNullable(map)
        .map(
            (v) -> {
              Map params =
                  v.entrySet().stream()
                      .filter((e) -> checkValue(e.getValue()))
                      .collect(Collectors.toMap((e) -> (String) e.getKey(), (e) -> e.getValue()));

              return params;
            })
        .orElse(null);
  }

  private static boolean checkValue(Object object) {

    if (object instanceof String && "".equals(object)) {
      return false;
    }

    if (null == object) {
      return false;
    }

    return true;
  }

  @Test
  void tt() {

    HashMap<String, String> props = Maps.newHashMap(Maps.fromProperties(System.getProperties()));

    Map<String, String> tenantMap = null;
    String domainSchemaPreKey = "java.";
    String tenantId = "hi";

    LogUtils.formatObjAndLogging(props, "tenantId:" + tenantId + ", domain <===> schema");

    tenantMap =
        props.entrySet().stream()
            .filter(e -> e.getKey().startsWith(domainSchemaPreKey))
            .collect(
                Collectors.toMap(
                    p -> p.getKey().substring(domainSchemaPreKey.length()), Map.Entry::getValue));
    // ParamsUtils.formatObjAndLogging(tenantMap, "tenantId:" + tenantId + ", domain <===> schema");
    LogUtils.formatObjAndLogging(tenantMap, "tenantId:" + tenantId + ", domain <===> schema");
  }

  @Test
  void m_map() {

    Map<String, String> m1 = Maps.newHashMap();
    Map<String, String> m2 = Maps.newHashMap();

    m1.put("v1", "v1");
    m2.put("v1", "v2");

    //m1.putAll(m2);
    m2.putAll(m1);

    m_map2(m2);

    LogUtils.formatObjAndLogging(m2, "hi");
  }


  void m_map2(Map<String, String> m){

    m = new HashMap<String, String>();

  }
}
