package com.st.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 各类json的相同转换, 排序等.
 *
 * <ul>
 *   <li>涉及的json: Gson, FastJson
 *   <li>转换: json->map
 * </ul>
 *
 * @author: st
 * @date: 2021/11/11 20:30
 * @version: 1.0
 * @description:
 */
public class JsonUtils {

  /**
   * 打印json格式字符串时, 进行美化, 方便查看. <p></p>
   *
   * 1, 将json格式的字符串({@link java.lang.String}),
   *    转成fastjson对象({@link com.alibaba.fastjson.JSONObject}) <br>
   *
   * 2, 用fastjson美化JSONObject <p>
   *
   * @param jsonStr json格式的字符串({@link java.lang.String})
   * @return fastsjson对象 {@link com.alibaba.fastjson.JSONObject}
   */
  public static JSONObject jsonStr2fastjsonObj(String jsonStr) {

    // System.out.println(JSON.toJSONString(jsonObject, true));

    return (JSONObject) JSONObject.parse(jsonStr);
  }
}
