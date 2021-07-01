package com.st.api.practice.gson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

/**
 * @author: st
 * @date: 2021/6/30 17:00
 * @version: 1.0
 * @description:
 */
@Slf4j
public class JsonSort {

  @Test
  public void jsonSort() {
    String str =
        "[{\"score\":77,\"id\":\"B04\"},{\"score\":88,\"id\":\"a01\"},{\"score\":66,\"id\":\"A01\"}]";
    System.out.println("排序前: \n" + str);
    JSONArray array = JSON.parseArray(str);

    // 输出: 传统, 丑陋
    for (Object arr : array) {
      log.info(arr.toString());
    }

    // 输出: 优雅
    log.info(JSONArray.toJSONString(array, true));

    log.info(
            "\n排序前:"
                    + "\n===============================================\n"
                    + "{}"
                    + "\n===============================================",
            JSONArray.toJSONString(array, true));


    // 单字段排序
     array.sort((a, b) -> ((JSONObject) a).getString("id").compareToIgnoreCase(((JSONObject)
         b).getString("id")));


    //array.sort(Comparator.comparing(json -> ((JSONObject) json).getString("id")));

    System.out.println("排序后,sort by id: \n" + array);


    log.info(
        "\n排序后,sort by id:"
            + "\n===============================================\n"
            + "{}"
            + "\n===============================================",
        JSONArray.toJSONString(array, true));
  }
}
