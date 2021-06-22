package com.st.api.practice.gson;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

/**
 * @author: st
 * @date: 2021/6/4 18:23
 * @version: 1.0
 * @description:
 */
@Slf4j
public class GsonFast {

	public static void main(String[] args) {
		String s = "{\n" +
				"  \"params\": [\n" +
				"    {\n" +
				"      \"items\": [\n" +
				"        {\n" +
				"          \"code\": \"p10\",\n" +
				"          \"defaultLabels\": \"p10\",\n" +
				"          \"dataType\": \"STRING\",\n" +
				"          \"defaultValues\": \"10\"\n" +
				"        }\n" +
				"      ]\n" +
				"    },\n" +
				"    {\n" +
				"      \"items\": [\n" +
				"        {\n" +
				"          \"code\": \"p11\",\n" +
				"          \"defaultLabels\": \"p11\",\n" +
				"          \"dataType\": \"STRING\",\n" +
				"          \"defaultValues\": \"11\"\n" +
				"        }\n" +
				"      ]\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		JsonParser jsonParser = new JsonParser();
		JsonObject  parse = (JsonObject) jsonParser.parse(s);

		JSONObject parse1 = (JSONObject) JSONObject.parse(parse.toString());

		JSONArray params = parse1.getJSONArray("params");

		System.out.println(params);

		System.out.println(s);

		List<HashMap> hashMaps = JSONObject.parseArray(params.toJSONString(), HashMap.class);
		//System.out.println(hashMaps);

		log.info(String.valueOf(hashMaps));

		List<HashMap> paramsx = hashMaps;
		log.info(String.valueOf(paramsx));


		//log.info(String.valueOf(JSONObject.parseObject(String.valueOf(paramsx))));
		//new JsonParser().parse((JsonReader) paramsx);
		//log.info(String.valueOf(new JsonParser().parse((JsonReader) paramsx)));





	}
}
