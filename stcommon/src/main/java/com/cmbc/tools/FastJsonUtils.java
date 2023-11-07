package com.cmbc.tools;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: st
 * @date: 2023/11/7 15:47
 * @version: 1.0
 * @description:
 */
public class FastJsonUtils {


	public static <S,T>  T  o2o(S source, Class<T> clazz) {
		String jsonString = JSONObject.toJSONString(source);
		T t = JSONObject.parseObject(jsonString, clazz);
		return t;
	}
}
