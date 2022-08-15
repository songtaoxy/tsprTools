package com.st.practice.file;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @author: st
 * @date: 2022/8/15 19:26
 * @version: 1.0
 * @description:
 */
public class DemoFile2 {

	public static void main(String[] args) throws IOException {
		String path = "/Users/songtao/downloads/x.txt";
		String pathy = "/Users/songtao/downloads/y.txt";
		String pathz = "/Users/songtao/downloads/z.txt";
		String pathf = "/Users/songtao/downloads/ff.txt";

		byte[] bytes = Files.readAllBytes(Paths.get(path));

		HashMap<String, Object> map = new HashMap<>();
		map.put("k1", "k1-value");
		map.put("c", bytes);

		map.put("d", new String(bytes, StandardCharsets.UTF_8));








		String s2 = new String(bytes);
		byte[] bytes3 = s2.getBytes(StandardCharsets.UTF_8);


		new FileOutputStream(pathy).write(bytes);

		//String s = JSON.toJSONString(map);
		String s = JSON.toJSONString(map);
		JSONObject jsonObject = JSON.parseObject(s);
		Object c = jsonObject.get("c");

		JSONArray x = (JSONArray) c;
		String s1 = x.toString();
		byte[] bytes2 = s1.getBytes(StandardCharsets.UTF_8);

		byte[] bytes1 = new byte[x.size()];
		
		for (int i = 0; i < x.size(); i++) {
			bytes1[i] = x.getByte(i);
		}








		new FileOutputStream(pathz).write(bytes1);


		String d = (String) jsonObject.get("d");

		new FileOutputStream(pathf).write(d.getBytes(StandardCharsets.UTF_8));


	}
}
