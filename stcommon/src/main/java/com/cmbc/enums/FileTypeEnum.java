package com.cmbc.enums;

import java.util.Objects;

public enum FileTypeEnum {


	PDF("1000","PDF","pdf",""),
	OFD("2000","OFD","OFD",""),
	PDF_OFD("3000","PDF_OFD","","pdf or ofd"),
	XML("4000","XML","xml",""),
	ZIP("5000","ZIP","zip",""),
	XML_ZIP("6000","XML_ZIP","","xml or zip");

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


	public static FileTypeEnum getEnumByCode(String code){

		if (code == null) {
			return null;
		}

		FileTypeEnum[] enumConstants = FileTypeEnum.class.getEnumConstants();

		for (FileTypeEnum enumItem : enumConstants) {
			if (Objects.equals(code, enumItem.getCode())) {
				return enumItem;
			}
		}
		return null;

	}
}
