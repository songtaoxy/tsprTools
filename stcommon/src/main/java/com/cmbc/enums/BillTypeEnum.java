package com.cmbc.enums;

public enum BillTypeEnum {


	PAPER_BILL("1000","纸质发票","",""),
	ELEC_BILL("2000","电子发票","",""),
	ELEC_BILL_PRO("2100","电子专票","",""),
	ELEC_BILL_GEN("2200","电子普票","",""),
	DELEC_BILL("3000","数电票","",""),
	DELEC_BILL_PRO("3100","数电专票","",""),
	DELEC_BILL_GEN("3200","数电普票","",""),
	TRAIN_BILL("4000","铁路发票","",""),
	AIR_BILL("5000","航空发票","","");

	private final String code;
	private final String name;
	private final String nameEN;
	private final String des;

	BillTypeEnum(String code, String name, String nameEN, String des) {
		this.code = code;
		this.name = name;
		this.nameEN = nameEN;
		this.des = des;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getNameEN() {
		return nameEN;
	}

	public String getDes() {
		return des;
	}
}
