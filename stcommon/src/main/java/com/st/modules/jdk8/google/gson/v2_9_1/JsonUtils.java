package com.st.modules.jdk8.google.gson.v2_9_1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Optional;

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
    result.put("flag", false);
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
   * 将JSON格式的字符串转成 {@link com.alibaba.fastjson.JSONObject} or {@link com.alibaba.fastjson.JSONArray}
   *
   * <p>应用场景: 打印日志中.如果格式非法,不抛出异常. <br>
   * 如果格式非法, 给出提示即可.<br>
   * 日志到处可用, 不能因为一个日志,就将整个调用链抛出异常.
   *
   * <p>
   *
   * @param content
   * @return
   */
  public static JSON str2json4Log(String content) {

    JSON json = null;

    if (JsonUtils.isJson(content)) {
      if (content.startsWith("{")) {
        json = JsonUtils.jsonStr2fastjsonObj(content);
      } else if (content.startsWith("[")) {
        json = JsonUtils.jsonStr2fastjsonArray(content);
      }
    }

    return Optional.ofNullable(json)
        .orElse(
            JsonUtils.jsonStr2fastjsonObj(
                "{\"tips\":\"the input param is illegal json format, check please.\"}"));
  }

  /**
   * 将JSON格式的字符串转成 {@link com.alibaba.fastjson.JSONObject} or {@link com.alibaba.fastjson.JSONArray}
   *
   * <p>应用场景: 业务中,格式非法, 则抛出异常.
   *
   * <p>
   *
   * @param content
   * @return
   */
  public static JSON str2json4Com(String content) {

    JSON json = null;

    if (JsonUtils.isJson(content)) {
      if (content.startsWith("{")) {
        json = JsonUtils.jsonStr2fastjsonObj(content);
      } else if (content.startsWith("[")) {
        json = JsonUtils.jsonStr2fastjsonArray(content);
      }
    }

    return Optional.ofNullable(json)
        .orElseThrow(() -> new IllegalArgumentException("the input param is illegal json format."));
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

  public static String jsonObj2String(){
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("k1", "v1");
    jsonObject.put("k2", "v2");
    System.out.println(jsonObject.toJSONString());
    return jsonObject.toJSONString();
  }


  public static void main(String[] args)  {
    String s2 = "[{\"name\":\"Michael\",\"age\":24,\"birthday\":\"2018-09-09\"}]";
    String s3 = "[{\"name\":\"Michael\",\"age\":24,\"birthday\":\"2018-09-09\"}];";

    try {
      System.out.println(JsonUtils.str2json4Com(s2));
      System.out.println(JsonUtils.str2json4Com(s3));
    } catch (Exception e) {

      /*
      e.printStackTrace();
      throw e;
      */
      System.out.println("无视异常");
    }

   /* System.out.println(JsonUtils.str2json4Com(s2));
    System.out.println(JsonUtils.str2json4Com(s3));*/
    System.out.println("h");

    jsonObj2String();
  }
}
