package com.st.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;

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
   * 打印json格式字符串时, 进行美化, 方便查看.
   *
   * <p>1, 将json格式的字符串({@link java.lang.String}), 转成fastjson对象({@link
   * com.alibaba.fastjson.JSONObject}) <br>
   * 2, 用fastjson美化JSONObject
   *
   * <p>
   *
   * @param jsonStr json格式的字符串({@link java.lang.String})
   * @return fastsjson对象 {@link com.alibaba.fastjson.JSONObject}
   */
  public static JSONObject jsonStr2fastjsonObj(String jsonStr) {

    // System.out.println(JSON.toJSONString(jsonObject, true));

    return (JSONObject) JSONObject.parse(jsonStr);
  }

  /**
   * 打印json格式字符串时, 进行美化, 方便查看.
   *
   * <p>1, 将json格式的字符串({@link java.lang.String}), 转成fastjson对象({@link
   * com.alibaba.fastjson.JSONObject}) <br>
   * 2, 用fastjson美化JSONObject
   *
   * <p>
   *
   * @param jsonStr json格式的字符串({@link java.lang.String})
   * @return fastsjson对象 {@link com.alibaba.fastjson.JSONObject}
   */
  public static JSONArray jsonStr2fastjsonArray(String jsonStr) {

    return (JSONArray) JSONArray.parse(jsonStr);
  }

  /**
   * 判断字符串是否为json格式
   *
   * <p>JSONObject是一个继承自JSON的类，当调用JSONObject.parseObject（result）时，会直接调用父类的parseObject（String
   * text）。parseObject（String text），将text解析为一个JSONObject对象并返回
   *
   * @param content
   * @return
   */
  public static HashMap<String, Object> jsonType(String content) {

    HashMap<String, Object> result = Maps.newHashMap();
    result.put("flag",false);
    result.put("type", "!Json");

    if (JsonUtils.isJson(content)) {
      if (content.startsWith("{")) {
        result.put("flag", true);
        result.put("type", "String_JsonObject");
      } else if (content.startsWith("[")) {
        result.put("flag", true);
        result.put("type", "String_JsonArray");
      }
    }

    return result;
  }

  /**
   * 判断字符串是否为json格式
   *
   * <p>JSONObject是一个继承自JSON的类，当调用JSONObject.parseObject（result）时，会直接调用父类的parseObject（String
   * text）。parseObject（String text），将text解析为一个JSONObject对象并返回
   *
   * @param content
   * @return
   */
  public static boolean isJson(String content) {

    if (StringUtils.isEmpty(content)) {
      return false;
    }
    boolean isJsonObject = true;
    boolean isJsonArray = true;
    try {
      JSONObject.parseObject(content);
    } catch (Exception e) {
      isJsonObject = false;
    }
    try {
      JSONObject.parseArray(content);
    } catch (Exception e) {
      isJsonArray = false;
    }
    if (!isJsonObject && !isJsonArray) { // 不是json格式
      return false;
    }
    return true;
  }
}
