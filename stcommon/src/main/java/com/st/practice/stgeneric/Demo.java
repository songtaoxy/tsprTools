package com.st.practice.stgeneric;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author: st
 * @date: 2022/11/16 09:34
 * @version: 1.0
 * @description:
 */
public class Demo {

	/*

	public static void main(String[] args) {
		Gson gson = new Gson();
		User user = new User();
		user.setUserId(1);
		user.setNickname("小书童");
		String jsonData = gson.toJson(user);
		System.out.println(String.format("jsonData:%s",jsonData));


		User newUser = gson.fromJson(jsonData,User.class);
		System.out.println(newUser);
	}

	*/


	public static void main(String[] args) {
		Gson gson = new Gson();
		ResMsg<User> resMsg = new ResMsg<>();
		User user = new User();
		user.setUserId(1);
		user.setNickname("小书童");
		resMsg.setData(user);
		String jsonData = gson.toJson(resMsg);
		System.out.println(String.format("jsonData:%s", jsonData));

		ResMsg resMsg1 = gson.fromJson(jsonData, ResMsg.class);

		ResMsg<User> newResMsg = gson.fromJson(jsonData, ResMsg.class);
		User newUser = newResMsg.getData();
		System.out.println(newUser);
	}

	/*

	public static void main(String[] args) {
		Gson gson = new Gson();
		ResMsg<User> resMsg = new ResMsg<>();
		User user = new User();
		user.setUserId(1);
		user.setNickname("小书童");
		resMsg.setData(user);
		String jsonData = gson.toJson(resMsg);
		System.out.println(String.format("jsonData:%s", jsonData));



		Type type = new TypeToken<ResMsg<User>>(){}.getType();

		ResMsg resMsg1 = gson.fromJson(jsonData, type);

		ResMsg<User> newResMsg = gson.fromJson(jsonData,type);

		User newUser = newResMsg.getData();


		System.out.println(newUser);
	}
	*/


}


class ResMsg<T> {

	private int code;
	private String msg;
	private T data;

	public ResMsg(){
		code = 0;
		msg = "成功";
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
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
}

class User extends Object {
	/**
	 * 用户id
	 */
	private int userId;
	/**
	 * 昵称
	 */
	private String nickname;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString(){
		return String.format("userId:%d , nickname:%s",userId,nickname);
	}
}
