package com.st.utils.stream;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.st.utils.bean.Employee;
import com.st.utils.log.LogUtils;
import one.util.streamex.EntryStream;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: st
 * @date: 2021/12/1 00:57
 * @version: 1.0
 * @description:
 */
public class StreamUtil {

  private static Map<String, Employee> map1 = new HashMap<>();
  private static Map<String, Employee> map2 = new HashMap<>();

  public static void main(String[] args) {
    mergeMap();
  }

  public static Map<String, Object> jsonStr2map(String str) {

    JSONObject jsonObject=  JSONObject.parseObject(str);

    Map<String, Object> map = (Map<String, Object>) jsonObject;


    return map;
  }



  public static void mergeMap(){

   /* Employee employee1 = new Employee(1L, "Henry");
    map1.put(employee1.getName(), employee1);
    Employee employee2 = new Employee(22L, "Annie");
    map1.put(employee2.getName(), employee2);
    Employee employee3 = new Employee(8L, "John");
    map1.put(employee3.getName(), employee3);

    Employee employee4 = new Employee(2L, "George");
    map2.put(employee4.getName(), employee4);
    Employee employee5 = new Employee(3L, "Henry");
    map2.put(employee5.getName(), employee5);*/

    HashMap<String, Map<String, String>> m1 = new HashMap<>();
    HashMap<String, Map<String, String>> m2 = new HashMap<>();

    HashMap<String,String> m3 = Maps.newHashMap();
    HashMap<String,String> m4 = Maps.newHashMap();
    m3.put("k3", "v3");
    m3.put("k4", "v5");
    m4.put("k4", "v4");

    m1.put("m3", m3);
    m2.put("m3", m4);

    Map<String, Map<String, String>> m5 = EntryStream.of(m1)
            .append(EntryStream.of(m2))
            .toMap((e1, e2) -> e1);




    Map<String, Map<String, String>> result = Stream.concat(m1.entrySet().stream(), m2.entrySet().stream())
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                    ));

   /* LogUtils.foal(m1,"map1");
    LogUtils.foal(m2,"map2");
    LogUtils.foal(m5,"map5");
    LogUtils.foal(result,"result");*/
  }
}
