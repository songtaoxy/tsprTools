package com.st.api.practice.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/6/18 16:09
 * @version: 1.0
 * @description:
 */
public class MapType {

	public static void main(String[] args) {
		String s = "100";
		Map<String, Object> objectObjectMap = new HashMap<>();

		objectObjectMap.put("v1", "k1");
		objectObjectMap.put("v2", null);


		HashMap<String, Object> objectObjectHashMap = new HashMap<>();

		objectObjectHashMap.put("s", s);
		objectObjectHashMap.put("map", objectObjectMap);

		Object map = objectObjectHashMap.get("map");
		if (map instanceof Map) {
			Map map1 = (Map) map;
			Object v1 = map1.get("v1");
			System.out.println(v1);
		}




	}
}
