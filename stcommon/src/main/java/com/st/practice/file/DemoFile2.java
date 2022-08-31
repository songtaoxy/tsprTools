package com.st.practice.file;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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
		String p = "/Users/songtao/downloads/2022-08-15_20-40-19.png";
		String p2 = "/Users/songtao/downloads/2022-08-15_20-40-20.png";
		String p3 = "/Users/songtao/downloads/2022-08-15_20-40-21.png";

		byte[] bytes = Files.readAllBytes(Paths.get(path));

		HashMap<String, Object> map = new HashMap<>();
		map.put("k1", "k1-value");
		map.put("c", bytes);

		map.put("d", new String(bytes, StandardCharsets.ISO_8859_1));


		byte[] bytes4 = Files.readAllBytes(Paths.get(p));
		String s3 = new String(bytes4,StandardCharsets.ISO_8859_1);
		byte[] bytes5 = s3.getBytes(StandardCharsets.ISO_8859_1);
		new FileOutputStream(p2).write(bytes5);
		new FileOutputStream(p3).write(bytes4);

		String s4 = Arrays.toString(bytes4);
		//new FileOutputStream(p2).write(s4.getBytes());
		//s4.getBytes()

		String s5 = Base64.encodeBase64String(bytes4);

		byte[] bytes6 = Base64.decodeBase64(s5);


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
