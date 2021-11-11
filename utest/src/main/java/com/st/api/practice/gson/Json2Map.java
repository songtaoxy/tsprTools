package com.st.api.practice.gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author: st
 * @date: 2021/6/18 13:49
 * @version: 1.0
 * @description:
 */
public class Json2Map {
	public static void main(String[] args) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("k1","v1");

		Gson gson = new Gson();
		Map map = gson.fromJson(jsonObject, Map.class);


		System.out.println(map);



	}
}
