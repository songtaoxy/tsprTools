package com.st.biz.cmbc.log;


import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/*@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor*/
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

	public LogEnumObj() {
	}

	public LogEnumObj(String title, String code, String project, String module, String action, String validation, String invalidation, String result, String detail, String ps) {
		this.title = title;
		this.code = code;
		this.project = project;
		this.module = module;
		this.action = action;
		this.validation = validation;
		this.invalidation = invalidation;
		this.result = result;
		this.detail = detail;
		this.ps = ps;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public String getInvalidation() {
		return invalidation;
	}

	public void setInvalidation(String invalidation) {
		this.invalidation = invalidation;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	@Override
	public String toString() {
		return "LogEnumObj{" +
				"title='" + title + '\'' +
				", code='" + code + '\'' +
				", project='" + project + '\'' +
				", module='" + module + '\'' +
				", action='" + action + '\'' +
				", validation='" + validation + '\'' +
				", invalidation='" + invalidation + '\'' +
				", result='" + result + '\'' +
				", detail='" + detail + '\'' +
				", ps='" + ps + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LogEnumObj that = (LogEnumObj) o;
		return Objects.equals(title, that.title) && Objects.equals(code, that.code) && Objects.equals(project, that.project) && Objects.equals(module, that.module) && Objects.equals(action, that.action) && Objects.equals(validation, that.validation) && Objects.equals(invalidation, that.invalidation) && Objects.equals(result, that.result) && Objects.equals(detail, that.detail) && Objects.equals(ps, that.ps);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, code, project, module, action, validation, invalidation, result, detail, ps);
	}
}
