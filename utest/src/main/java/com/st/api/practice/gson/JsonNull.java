package com.st.api.practice.gson;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author: st
 * @date: 2021/6/18 16:47
 * @version: 1.0
 * @description:
 */
public class JsonNull {
	public static void main(String[] args) {
		JsonObject jsonObject = new JsonObject();

		JsonObject jsonObject1 = null;

		jsonObject.add("x",jsonObject1);

		String s = jsonObject.toString();

		JsonElement jsonElement = jsonObject.get("x");

		System.out.println(jsonElement.toString());

		System.out.println(s);


		Boolean b = null;
		Optional<Boolean> optionalB = Optional.ofNullable(b);


		jsonObject.addProperty("y", (String) null);
		jsonObject.addProperty("z", (String) null);

		System.out.println(jsonObject.toString());

		//System.out.println(String.valueOf(null));

		System.out.println((String)null );


		String a = "/a/b/c";
		System.out.println(a+"/d/e");


		Map map = new HashMap<>();
		map = null;
		//Preconditions.checkNotNull(map.size()>0,"hav no elemetns");
		Preconditions.checkArgument(!(null ==map || map.size()==0),"hav no elemetns");
	}
}
