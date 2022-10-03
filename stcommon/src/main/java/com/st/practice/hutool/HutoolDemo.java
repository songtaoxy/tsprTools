package com.st.practice.hutool;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: st
 * @date: 2022/10/3 16:52
 * @version: 1.0
 * @description:
 */
public class HutoolDemo {
	public static void main(String[] args) {

		strUtilT();
	}

	public static void strUtilT() {

		String s = "{} 很想测试这个工具, {}, And do you have?";
		String format = StrUtil.format(s, "I", "you");
		System.out.println(format);

		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");

		JSONObject o = new JSONObject();
		o.put("k",list);

		List<String> types = o.getObject("k",List.class);
		System.out.println(JSON.toJSONString(types));

		List<String> types2 = o.getObject("k",new TypeReference<List<String>>(){});
		System.out.println(JSON.toJSONString(types2));

	}
}
