package com.st.modules.log;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
/**
 * <li> 枚举类对应的实体</li>
 * <li> see {@link LogEnum}</li>
 */
public class LogEnumObj {

	/*--------------title---------------*/
	private  String title;
	/*--------------code---------------*/
	private  String code;
	/*--------------项目---------------*/
	private  String project;
	/*--------------模块---------------*/
	private String module;
	/*--------------行为---------------*/
	private String action;
	/*----------行为:结果.预期-----------*/
	private String validation;
	/*--------行为:结果.非预期-----------*/
	private String invalidation;
	/*----------行为:结果.实际-----------*/
	private String result;
	/*----------行为:结果.详情----------*/
	private String detail;
	/*--------------备注---------------*/
	private String ps;



}
