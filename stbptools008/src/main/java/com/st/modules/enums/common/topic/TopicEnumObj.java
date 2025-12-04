package com.st.modules.enums.common.topic;


import com.st.modules.log.LogEnum;
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
public class TopicEnumObj {

	/*--------------code---------------*/
	private String code;
	/*--------------title---------------*/
	private  String service;
	/*--------------项目---------------*/
	private  String system;
	/*--------------模块---------------*/
	private  String module;
	/*--------------步骤---------------*/
	private  String step;
	/*--------------行为---------------*/
	private  String name;
	/*    *//*----------行为:结果.预期-----------*//*
        private final String validation;
        *//*--------行为:结果.非预期-----------*//*
        private final String invalidation;
        *//*----------行为:结果.实际-----------*//*
        private final String result;
        *//*----------行为:结果.详情----------*/
	private  String detail;
	/*--------------备注---------------*/
	private  String des;


	public String buildTopic () {

		String finalTopic = null;

		String split="/";
		String service1 = this.getService();
		String system1 = this.getSystem();
		String module1 = this.getModule();
		String step1 = this.getStep();
		String name1 = this.getName();
		String detail1 = this.getDetail();
		String des1 = this.getDes();
		finalTopic = split+service1+split+system1+split+module1+split+module1+split+step1+split+ name1;
		if(null!=detail1 && !"".equalsIgnoreCase(detail1)){
			finalTopic=finalTopic+split+detail1;
		}

		if (null!=des1 && !"".equalsIgnoreCase(des1)) {
			finalTopic=finalTopic+split+des1;
		}


		return finalTopic;
	}
}
