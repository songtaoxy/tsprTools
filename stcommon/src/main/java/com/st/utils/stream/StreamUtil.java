package com.st.utils.stream;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.Var;
import lombok.var;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/12/1 00:57
 * @version: 1.0
 * @description:
 */
public class StreamUtil {

  public static Map<String, Object> jsonStr2map(String str) {

    JSONObject jsonObject=  JSONObject.parseObject(str);

    Map<String, Object> map = (Map<String, Object>) jsonObject;


    return map;
  }
}
