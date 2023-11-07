package com.cmbc.log;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.st.utils.json.gson.GsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author: st
 * @date: 2022/9/2 14:51
 * @version: 1.0
 * @description:
 */
@Slf4j
public class LogUtils {
  /**
   * 打印日志,并格式化
   *
   * @param des     描述信息
   * @param jsonStr 日志具体信息; json格式的字符串
   */
  public static void printJsonStr(String des, String jsonStr) {
    String des1 = Optional.ofNullable(des).orElse("Des");

    // ========================
    // validate
    // ========================
    String format = StrUtil.format("input :: jsonObject[{}] can't be null", jsonStr);
    Preconditions.checkArgument(ObjectUtil.isNotEmpty(jsonStr), format);


    // ========================
    // build target
    // ========================
    String doubleLine = "===============================";
    String line = System.lineSeparator();
    String brace = "{}";
    String des2 = line + des1 + line + doubleLine + line + brace + line + doubleLine;


    // ========================
    // JsonObject jsonObject -> String str
    // and pretty str
    // ========================
    GsonBuilder builder = GsonUtils.builder();
    builder.setPrettyPrinting();
    Gson gson = builder.create();

    JsonObject parse = GsonUtils.parse(jsonStr, JsonObject.class);
    String prettyJsonStr = gson.toJson(parse);

    // ========================
    // output
    // ========================
    log.info(des2, prettyJsonStr);
  }

  /**
   * 打印日志,并格式化
   *
   * @param des        描述信息
   * @param jsonObject 日志具体信息
   */
  public static void printGson(String des, JsonElement jsonObject) {
    String des1 = Optional.ofNullable(des).orElse("Des");
    //JSONObject jsonObject1 = Optional.ofNullable(jsonObject).orElse(new JSONObject());

    // ========================
    // validate
    // ========================
		/*
		String format = StrUtil.format("input :: jsonObject[{}] can't be null", jsonObject);
		Preconditions.checkArgument(ObjectUtil.isNotEmpty(jsonObject), format);
		*/

    String flag="0";
    JsonObject jsonObject1 = null;
    if (ObjectUtil.isEmpty(jsonObject)) {
      flag = "1";
      jsonObject1 = new JsonObject();
      jsonObject1.addProperty("arguments","入参为:["+jsonObject+"]");
      jsonObject1.addProperty("des","重新构建的对象, 仅为打印");

    }




    // ========================
    // build target
    // ========================
    String doubleLine = "===============================";
    String line = System.lineSeparator();
    String brace = "{}";
    String des2 = line + des1 + line + doubleLine + line + brace + line + doubleLine;


    // ========================
    // JsonObject jsonObject -> String str
    // and pretty str
    // ========================
    GsonBuilder builder = GsonUtils.builder();
    builder.setPrettyPrinting();
    //builder.serializeNulls();
    Gson gson = builder.create();


    String prettyJsonStr;
    if (ObjectUtil.equals("1", flag)) {
      prettyJsonStr = gson.toJson(jsonObject1);
    } else {
      prettyJsonStr = gson.toJson(jsonObject);
    }

    // ========================
    // output
    // ========================
    log.info(des2, prettyJsonStr);
  }

  /**
   * 打印日志,并格式化
   *
   * @param des        描述信息
   * @param jsonObject 日志具体信息
   */
  /*public static void printJson(String des, JSONObject jsonObject) {
    String des1 = Optional.ofNullable(des).orElse("Des");
    JSONObject jsonObject1 = Optional.ofNullable(jsonObject).orElse(new JSONObject());

    String doubleLine = "===============================";
    String line = System.lineSeparator();
    String brace = "{}";
    String des2 = line + des1 + line + doubleLine + line + brace + line + doubleLine;

    log.info(des2, JSONObject.toJSONString(jsonObject1, JSONWriter.Feature.PrettyFormat));
  }*/


  /**
   * 打印字符串
   * <li>如果是json格式, 则打印成json格式</li>
   * <li>如果是非json格式, 则正常打印</li>
   *
   * @param des
   * @param logContent
   */
  /*public static void printString(String des, String logContent) {
    String des1 = Optional.ofNullable(des).orElse("Des");
    String jsonObject1 = Optional.ofNullable(logContent).orElse(String.valueOf(new JSONObject()));

    // 说明是json形式的字符串; 否则就是普通字符串
    if (strIsorNotJSON2(des, jsonObject1)) {
      JSONObject jsonObject = JSONObject.parseObject(jsonObject1);
      printJson(des1, jsonObject);
    } else {

      String doubleLine = "===============================";
      String line = System.lineSeparator();
      String brace = "{}";
      String des2 = line + des1 + line + doubleLine + line + brace + line + doubleLine;

      log.info(des2, jsonObject1);
    }
  }*/


  /**
   * 打印非json字符串
   *
   * @param des
   * @param logContent
   */
  public static void printNJStr(String des, String logContent) {
    String des1 = Optional.ofNullable(des).orElse("Des");
    String jsonObject1 = Optional.ofNullable(logContent).orElse(String.valueOf(new JSONObject()));

    String doubleLine = "===============================";
    String line = System.lineSeparator();
    String brace = "{}";
    String des2 = line + des1 + line + doubleLine + line + brace + line + doubleLine;

    log.info(des2, jsonObject1);
  }


  public static boolean strIsorNotJSON2(String des, String str) {
    boolean result = false;
    try {
      Object obj = JSON.parse(str);
      result = true;
    } catch (Exception e) {
      log.error("日志:[{}]入参中, {}不是合法的json; err_message:{}; details:{}", des, str, e.getMessage(), e);
      result = false;
    }
    return result;
  }

	/*
	public static void main(String[] args) {
			*/
/*
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("a", "b");

		printString(null,null);
		printString("hi","ssss");
		printString("hi",  jsonObject.toJSONString());
		*/    /*


   */
/*
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("k1","v1");
		jsonObject.addProperty("k2","v2");

		printGson(null,jsonObject);

		printJsonStr(null,jsonObject.toString());
		*/	/*

	}
	*/

  /**
   *
   * <li>打印结构化日志</li>
   * <li>opt_type: 操作类型</li>
   * <li>opt_result: 操作结果</li>
   *
   * <ul>details_content: 操作获取的某个结果, 具体内容
   * <li>异常-不符合预期: null</li>
   * <li>异常-不符合预期: 空</li>
   * <li>正常-符合预期: 无需传入值.</li>
   * </ul>
   *
   *
   * @param opt_type
   * @param opt_result
   * @param details_content
   */
  public static void optionalLog(String opt_type, String opt_result, String details_content) {


    String details;

    if (ObjectUtil.isEmpty(details_content)) {
      JsonObject jsonObject = GsonUtils.buildGJS();
      jsonObject.addProperty("result", "null,blank,or optional");
      details = jsonObject.toString();
    } else {
      details = details_content;
    }

    // ===================
    // optional log
    // ===================
    JsonObject js = GsonUtils.buildGJS();
    js.addProperty("opt_type", opt_type);
    js.addProperty("opt_result", opt_result);
    js.addProperty("details", details);
    LogUtils.printGson(opt_type, js);
  }


}
