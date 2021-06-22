package com.st.api.practice.gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;

/**
 * @author: st
 * @date: 2021/5/26 10:18
 * @version: 1.0
 * @description:
 */
@Slf4j
public class GsonReader {

    /*String serviceCode = "serviceCode";
    String errorDetails = "errorDetails";
    String local_metaclass = "local_metaclass";
    String uri = "uri";*/

	public static void main(String[] args) {
		//gsonReader();
	}

	public static void gsonReader() {

		String serviceCode = "serviceCode";
		String errorDetails = "errorDetails";
		String local_metaclass = "local_metaclass";
//        String uri = "GT69287AT56.report.test_script_005";
		String uri = ".report.zzlc";
		String jsonStr = "{\"params\": {" +
				"\"ctx\": {" +
				"\"serviceCode\": \"" + serviceCode + "\"" +
				"}," +

				"\"bz\": {" +
				"\"serviceCode\": \"" + serviceCode + "\"" +
				"}," +


				"\"msgError\": {" +
//                "\"serviceCode\": \"" + serviceCode + "\"" +
				"\"errorDetails\": \"" + errorDetails + "\"" +
				"}," +


				"\"metaClassFlag\": \"" + local_metaclass + "\"," +
				"\"modelParam\": {}" +
				"},\"pager\": { \"pageIndex\": 1, \"pageSize\": 200,\"pageCount\": 0 }, \"entity\": \"" + uri + "\" }";

		JsonReader reader = new JsonReader(new StringReader(jsonStr));
		reader.setLenient(true);
		JsonObject queryParam = (JsonObject) new JsonParser().parse(reader);
		log.info("queryParam is:{}", queryParam);
	}

}
