package com.st.api.practice.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: st
 * @date: 2021/6/7 15:03
 * @version: 1.0
 * @description:
 */
@Slf4j
public class StringAarryTest {

	public static void main(String[] args) {
		strArray();
	}

	public static void strArray(){

		JsonObject jsonObject = new JsonObject();

		String[] values = new String[]{"p11,p11-ext"};
		JsonArray jsonArray = new JsonArray();
		for (String ele : values) {
			jsonArray.add(ele);
		}



		jsonObject.addProperty("k1","v1");
		jsonObject.add("defaultValues",jsonArray);


		JsonArray jsonArray1 = new JsonArray();
		jsonArray1.add("p12,12");

		jsonObject.add("12",jsonArray1);


		JsonArray labelsArray = new JsonArray();
		jsonObject.add("defaultLabes", labelsArray);

		log.info(String.valueOf(jsonObject));

		//JsonElement parse = new JsonParser().parse(jsonObject);

	}
}
