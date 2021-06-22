package com.st.api.practice.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: st
 * @date: 2021/6/10 21:09
 * @version: 1.0
 * @description:
 */
@Slf4j
public class JsonReplace {

	public static void main(String[] args) {


		JsonObject jsonObject = new JsonObject();

		JsonArray jsonArray = new JsonArray();
		JsonObject jsonObject1 = new JsonObject();
		jsonObject1.addProperty("j1","v1");
		jsonArray.add(jsonObject1);


		JsonArray jsonArray2 = new JsonArray();
		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.addProperty("j2","v2");
		jsonArray2.add(jsonObject2);

		jsonObject.addProperty("k1", "v1");
		jsonObject.addProperty("k1", "v2");

		jsonObject.add("a", jsonArray);
		jsonObject.add("a", jsonArray2);

		log.info(String.valueOf(jsonObject));

		JsonArray jsonObject3 = jsonObject.getAsJsonArray( "a");
		JsonObject jsonObject4 = new JsonObject();
		jsonObject4.addProperty("k4","v4");

		jsonObject3.add(jsonObject4);

		log.info(String.valueOf(jsonObject));


	}
}
