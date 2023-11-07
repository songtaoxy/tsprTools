package com.cmbc.enums;

public enum FileTypeEnum {


	PDF("1000","PDF","pdf",""),
	OFD("2000","OFD","OFD",""),
	XML("3000","XML","xml",""),
	ZIP("4000","ZIP","zip","");

	private final String code;
	private final String name;
	private final String suffix;
	private final String des;

	FileTypeEnum(String code, String name, String suffix, String des) {
		this.code = code;
		this.name = name;
		this.suffix = suffix;
		this.des = des;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getDes() {
		return des;
	}
}
