package com.st.api.practice.gson;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;


/**
 * @author: st
 * @date: 2021/6/4 11:10
 * @version: 1.0
 * @description:
 */
public class IntStr {


	public static void main(String[] args) {

		//intStr();
		jt();
	}

	public static void intStr() {

		HashMap<Object, Object> objectObjectHashMap = new HashMap<>();

		objectObjectHashMap.put("resultCode", 200);
		if ("200".equals(objectObjectHashMap.get("resultCode") + "")) {
			System.out.println("yes");

		}

	}

	public static void jt() {
		String result = "{\"data\":{\"_id\":{\"$oid\":\"60b883b086d8dd005968bef9\"},\"id\":\"d16d76c7-4f73-492d-bb04-793529ffc442\",\"tenantId\":\"edr19n2r\",\"uri\":\"GT69287AT56.report.test_script_008\",\"paramList\":[{\"code\":\"p10\",\"name\":\"p10\",\"oper\":\"IN\",\"dataType\":\"STRING\",\"must\":false,\"rule\":true,\"showType\":0,\"value\":\"10000\",\"using\":false,\"key\":\"0b9e5bf8-6f67-43f4-a1b7-152fface67c2\",\"uid\":\"febd6140-5d1b-4fc2-815f-fba3d699b860\",\"sources\":{},\"description\":\"p10\",\"defultValue\":\"10\"},{\"code\":\"p11\",\"name\":\"p11\",\"oper\":\"IN\",\"dataType\":\"STRING\",\"must\":false,\"rule\":true,\"showType\":0,\"value\":\"111111\",\"using\":false,\"key\":\"b9c22a76-c35f-4a66-9427-ae6814651fdc\",\"uid\":\"75f5c4bf-29ad-4742-adbf-a4c7f021b96b\",\"sources\":{},\"description\":\"p11\",\"defultValue\":\"11\"}],\"createTime\":\"2021-06-03 15:24:32:866\",\"modifyTime\":\"2021-06-03 15:24:32:866\"},\"resultCode\":200,\"message\":\"success\"}";

		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(result);

		String resultCode = jsonObject.get("resultCode").getAsString();
		//JsonElement resultode = jsonObject.get("resultode");

		System.out.println(resultCode);

		JSONArray jsonArray = new JSONArray();
		//if("200".equals(JsonObject.get(jsonObject,"resultCode")+"")) {
		if ("200".equals(resultCode + "")) {
			JSONObject jsonNew = (JSONObject) JSONObject.parse((jsonObject.toString()));
			System.out.println(jsonNew.toString());

			JSONObject data = jsonNew.getJSONObject("data");

			JSONArray params = data.getJSONArray("paramList");

			jsonArray = params;
			System.out.println(jsonArray);
		}

		JsonObject jsonObject1 = buildFormatedJson();
		System.out.println(jsonObject1.toString());

	}


	public static JsonObject buildFormatedJson() {
		JsonObject addtionalElements = new JsonObject();

		JsonArray paramsArray = new JsonArray();
		addtionalElements.add("params", paramsArray);


		return addtionalElements;
	}
	}
