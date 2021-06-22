package com.st.api.practice.gson;

import com.google.gson.*;

import javax.naming.PartialResultException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/6/3 16:36
 * @version: 1.0
 * @description:
 */
public class StrReg {

	public static void main(String[] args) {
		str();
		//str2();
	}


	public static void str() {

		String s2 = "{ \"_id\" : { \"$oid\" : \"60b883b086d8dd005968bef9\" }, \"id\" : \"d16d76c7-4f73-492d-bb04-793529ffc442\", \"tenantId\" : \"edr19n2r\", \"uri\" : \"GT69287AT56.report.test_script_008\", \"paramList\" : [{ \"code\" : \"p10\", \"name\" : \"p10\", \"oper\" : \"IN\", \"dataType\" : \"STRING\", \"must\" : false, \"rule\" : true, \"showType\" : 0, \"value\" : \"10000\", \"using\" : false, \"key\" : \"0b9e5bf8-6f67-43f4-a1b7-152fface67c2\", \"uid\" : \"febd6140-5d1b-4fc2-815f-fba3d699b860\", \"sources\" : { }, \"description\" : \"p10\", \"defultValue\" : \"10\" }, { \"code\" : \"p11\", \"name\" : \"p11\", \"oper\" : \"IN\", \"dataType\" : \"STRING\", \"must\" : false, \"rule\" : true, \"showType\" : 0, \"value\" : \"111111\", \"using\" : false, \"key\" : \"b9c22a76-c35f-4a66-9427-ae6814651fdc\", \"uid\" : \"75f5c4bf-29ad-4742-adbf-a4c7f021b96b\", \"sources\" : { }, \"description\" : \"p11\", \"defultValue\" : \"11\" }], \"createTime\" : \"2021-06-03 15:24:32:866\", \"modifyTime\" : \"2021-06-03 15:24:32:866\" }";


		Map<String, Object> params = new HashMap<>();
		params.put("data1", s2);

		JsonParser jsonParser = new JsonParser();
		JsonObject parse = (JsonObject) jsonParser.parse(s2);
		params.put("data3", parse);

		Gson gson = new GsonBuilder().create();
		String content = gson.toJson(params);
		System.out.println(content);
	}

	public static void str2() {
		Map<String, String> params = new HashMap<>();
		params.put("key1", "value1");
		params.put("key2", "value2");
		params.put("key3", "value3");


		Gson gson = new GsonBuilder().create();

		String content = gson.toJson(params);
		System.out.println(content);
	}


}