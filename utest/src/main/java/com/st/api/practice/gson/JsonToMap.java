package com.st.api.practice.gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/6/15 15:11
 * @version: 1.0
 * @description:
 */
public class JsonToMap {
	public static void main(String[] args) {


		String json = "{\n" +
				"  \"groups\": [],\n" +
				"  \"params\": {\n" +
				"    \"ctx\": {\n" +
				"      \"tenant_id\": \"apjp3kqe\",\n" +
				"      \"serviceCode\": \"intellivworkbenchplatform-22\",\n" +
				"      \"locale\": \"zh_CN\",\n" +
				"      \"user_id\": \"a5d5367b-7230-4023-a2d9-d2279895aeb2\",\n" +
				"      \"systenant\": \"U8C3\"\n" +
				"    },\n" +
				"    \"bz\": {\n" +
				"      \"serviceCode\": \"intellivworkbenchplatform-22\",\n" +
				"      \"currentqty\": \"50\",\n" +
				"      \"locale\": \"zh_CN\",\n" +
				"      \"queryId\": \"apjp3kqe|||1623739255518\",\n" +
				"      \"vtrace\": \"1623739255546sc17pz4el2d\"\n" +
				"    },\n" +
				"    \"modelParam\": {\n" +
				"      \"currentqty\": \"50\"\n" +
				"    }\n" +
				"  },\n" +
				"  \"queryType\": \"null-bfde6e6b-9e7b-44ca-85ac-47652e97ed0c\",\n" +
				"  \"havings\": [],\n" +
				"  \"calcFlag\": \"query\",\n" +
				"  \"pager\": {\n" +
				"    \"pageCount\": 0,\n" +
				"    \"recordCount\": 0,\n" +
				"    \"pageSize\": 20,\n" +
				"    \"pageIndex\": 0\n" +
				"  },\n" +
				"  \"orders\": [],\n" +
				"  \"fields\": [\n" +
				"    {\n" +
				"      \"name\": \"id\",\n" +
				"      \"alias\": \"id_NONE_\",\n" +
				"      \"aggr\": \"\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"org\",\n" +
				"      \"alias\": \"org_NONE_\",\n" +
				"      \"aggr\": \"\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"product\",\n" +
				"      \"alias\": \"product_NONE_\",\n" +
				"      \"aggr\": \"\"\n" +
				"    }\n" +
				"  ],\n" +
				"  \"conditions\": [],\n" +
				"  \"entity\": \"custome-report.report.model-test-0609-4\"\n" +
				"}";

		//Map map = new HashMap();
		//Map map1 = new Gson().fromJson(json, Map.class);
		//System.out.println(map1);


		//String paramJsonStr = BaseType2Json.simpleObject2Json(param);
		JsonObject paramJson = new JsonParser().parse(json).getAsJsonObject();
		//JsonObject params = JsonObjectUtils.getJsonObject(paramJson, "params");

		JsonObject params = paramJson.get("params").getAsJsonObject();
		String s = params.toString();

		Map map = new HashMap();
		Map map1 = new Gson().fromJson(s, Map.class);
		System.out.println(map1);
		System.out.println("====================");


		Map modelParam = (Map) map1.get("modelParam");

		System.out.println(map1.get("modelParam"));
		System.out.println(modelParam);
		System.out.println(modelParam.get("currentqty"));

	}
}
