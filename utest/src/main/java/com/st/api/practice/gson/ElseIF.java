package com.st.api.practice.gson;



import java.util.HashMap;

/**
 * @author: st
 * @date: 2021/6/4 14:16
 * @version: 1.0
 * @description:
 */
public class ElseIF {

	public static void main(String[] args) {

		HashMap<String,String> objectObjectHashMap = new HashMap<>();

		//String data_auth = "data_auth";
		//String data_auth = "data_auth_";
		String data_auth = null;


		if (null == data_auth) {
			data_auth = "";
		} else if ("data_auth".equals(data_auth)) { // 客户要求改成中文, 因为看不懂english
			data_auth = "权限受控";
		}
		objectObjectHashMap.put("data_auth", data_auth);


		System.out.println(objectObjectHashMap.get("data_auth"));

	}

	void m(){
		System.out.println("hi");
	}
}
