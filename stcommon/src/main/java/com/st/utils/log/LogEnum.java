package com.st.utils.log;


import com.google.gson.JsonObject;
import com.st.utils.json.gson.GsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Getter
@AllArgsConstructor
@Slf4j
/**
 * <li>各类日志字典</li>
 * <li>预定义字段</li>
 * <li>see: {@link LogEnumObj}</li>
 */
public enum LogEnum {

	/**
	 * <li> 通用</li>
	 */
	L_general("general", "00000", "RAP", "DCC", null, null, null, null, null, null),
	/**
	 * log.gson: List<T> -> List<R>
	 */
	L_gson_convert_listObjT2ListR("log.gson: List<T> -> List<R>", "10000", "RAP", "DCC", "convert", null, "input arguments is not validate", null, null, null),

	/**
	 * 校验工单中的故障设备是否已经全部恢复
	 */
	dcc_warn_validate("校验工单中的故障设备是否已经全部恢复", "10001", "RAP", "dcc.warning", "validate", "所有故障全部恢复", "故障未全部恢复", null, null, null),
	/**
	 * 创建工单详情: 故障_工单_详情
	 */
	fault_ticket("创建工单详情: 故障_工单_详情", "10002", "RAP", "fault.ticket", "写入工单描述信息", null, null, null, null, null);



	/*--------------title---------------*/
	private final String title;
	/*--------------code---------------*/
	private final String code;
	/*--------------项目---------------*/
	private final String project;
	/*--------------模块---------------*/
	private final String module;
	/*--------------行为---------------*/
	private final String action;
	/*----------行为:结果.预期-----------*/
	private final String validation;
	/*--------行为:结果.非预期-----------*/
	private final String invalidation;
	/*----------行为:结果.实际-----------*/
	private final String result;
	/*----------行为:结果.详情----------*/
	private final String detail;
	/*--------------备注---------------*/
	private final String ps;


	@SneakyThrows
	public static <T> LogEnumObj buildJson(T  logEnum) {

		JsonObject js = GsonUtils.buildGJS();

		for (Field field : logEnum.getClass().getDeclaredFields()) {
			//把私有属性公有化
			field.setAccessible(true);

			Class<?> type = field.getType();

			//if (!type.isEnum()) {
			if (type.equals(String.class)) {
				String name = field.getName();
				String value = (String) field.get(logEnum);
				js.addProperty(name, value);

			}
		}
		LogEnumObj logEnumObj = GsonUtils.o2o(js, LogEnumObj.class);

		return logEnumObj;
	}

	public static void main(String[] args) {
		LogEnumObj jsonObject = buildJson(LogEnum.L_gson_convert_listObjT2ListR);


		JsonObject jsonObject1 = GsonUtils.o2o(jsonObject, JsonObject.class);

		LogUtils.printGson("test", jsonObject1);

		//Object o = GsonUtils.convertBean(LogEnum.L_gson_convert_listObjT2ListR, JsonObject.class);

		log.info(GsonUtils.toJson(LogEnum.L_gson_convert_listObjT2ListR));



	}
}
