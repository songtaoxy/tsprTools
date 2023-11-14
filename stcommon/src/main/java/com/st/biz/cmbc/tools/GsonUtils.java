package com.st.biz.cmbc.tools;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.st.biz.cmbc.log.LogEnum;
import com.st.biz.cmbc.log.LogUtils;
import com.google.common.base.Preconditions;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Data
@Slf4j
class Result<T> {
	/**
	 * 状态码
	 */
	private Integer code;
	/**
	 * 状态
	 */
	private Boolean success;
	/**
	 * 返回消息
	 */
	private String msg;
	/**
	 * 数据
	 */
	private T data;

	public Result() {
	}

	public Result(Integer code, Boolean success, String msg, T data) {
		this.code = code;
		this.success = success;
		this.msg = msg;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Result{" +
				"code=" + code +
				", success=" + success +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}

//@Data
@Slf4j
class Result2 {
	/**
	 * 状态码
	 */
	private Integer code;
	/**
	 * 状态
	 */
	private Boolean success;
	/**
	 * 返回消息
	 */
	private String msg;
	/**
	 * 数据
	 */
	//private T data;


	public Result2() {
	}

	public Result2(Integer code, Boolean success, String msg) {
		this.code = code;
		this.success = success;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "Result2{" +
				"code=" + code +
				", success=" + success +
				", msg='" + msg + '\'' +
				'}';
	}
}

//@Data
class TokenInfo {
	/**
	 * 过期时间
	 */
	private Long expire;
	/**
	 * Token
	 */
	public String token;
	/**
	 * 类型
	 */
	private String type;

	public TokenInfo() {
	}

	public TokenInfo(Long expire, String token, String type) {
		this.expire = expire;
		this.token = token;
		this.type = type;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "TokenInfo{" +
				"expire=" + expire +
				", token='" + token + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}





