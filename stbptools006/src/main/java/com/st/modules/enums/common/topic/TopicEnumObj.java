package com.st.modules.enums.common.topic;




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

	public TopicEnumObj() {
	}


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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	@Override
	public String toString() {
		return "TopicEnumObj{" +
				"code='" + code + '\'' +
				", service='" + service + '\'' +
				", system='" + system + '\'' +
				", module='" + module + '\'' +
				", step='" + step + '\'' +
				", name='" + name + '\'' +
				", detail='" + detail + '\'' +
				", des='" + des + '\'' +
				'}';
	}
}
