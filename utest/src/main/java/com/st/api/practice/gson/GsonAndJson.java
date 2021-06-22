package com.st.api.practice.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: st
 * @date: 2021/6/2 15:08
 * @version: 1.0
 * @description:
 */
@Slf4j
public class GsonAndJson {

	public static void main(String[] args) {
		hasTest();
	}

	public static void hasTest() {

		JsonObject jsonObject = new JsonObject();


		JsonArray jsonArray = new JsonArray();
		JsonObject jsonObject1 = new JsonObject();
		jsonObject.addProperty("k1", "v1");
		jsonArray.add(jsonObject1);

		jsonObject.add("paramList", jsonArray);

		boolean paramList = jsonObject.has("paramList");

		log.info(String.valueOf(paramList));
	}
}