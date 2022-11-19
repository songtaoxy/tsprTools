package com.st.practice.stgeneric;

import cn.hutool.core.io.resource.ResourceUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * @author: st
 * @date: 2022/11/18 21:04
 * @version: 1.0
 * @description:
 */
@Data
@Slf4j
public class Result<T> {
	/** 状态码 */
	private Integer code;
	/** 状态 */
	private Boolean success;
	/** 返回消息 */
	private String msg;
	/** 数据 */
	private T data;


	public static void main(String[] args) {

		//String content = ResourceUtil.readUtf8Str("object.json");
		String content = "{\n" +
				"   \"code\": 10000,\n" +
				"   \"success\": true,\n" +
				"   \"msg\": \"token初始化成功\",\n" +
				"   \"data\": {\n" +
				"     \"expire\": 80215,\n" +
				"     \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiM\",\n" +
				"     \"type\": \"free\"\n" +
				"   }\n" +
				" }\n";


		log.info(content);


		Type type = new TypeToken<Result<TokenInfo>>(){}.getType();
		Result<TokenInfo> result = new Gson().fromJson(content, type);

		log.info(result.toString());
	}

}


@Data
class TokenInfo {
	/** 过期时间 */
	private Long expire;
	/** Token */
	public String token;
	/** 类型 */
	private String type;
}
