package com.st.modules.file.wp.mogo;

/**
 * @author: st
 * @date: 2023/11/6 13:27
 * @version: 1.0
 * @description:
 */

/**
 * <li>接收oa传入文件列表</li>
 * <li>该文件在业务过程中的状态信息</li>
 */
public class FilesInputEntry {
	/**
	 * 文件名称
	 */
	private String name;
	/**
	 * 文件类型: pdf, ofd, zip, xml
	 */
	private String type;

	/**
	 * 上传影像平台
	 */
	private String imageId;
	/**
	 * 验签状态
	 */
	private String validateSingnStatus;
	/**
	 * 发票类型
	 */
	private String billType;
	/**
	 * <li>文件md5</li>
	 * <li>pdf, ofd 必须</li>
	 * <li>xml, zip 非必须</li>
	 */
	private String md5;

	private String ps;

	private String billCode;
	private String billNumber;



	public FilesInputEntry() {
	}
	public FilesInputEntry(String name, String type, String contentsStr) {
		this.name = name;
		this.type = type;
	}

	public FilesInputEntry(String name, String type, String contentsStr, byte[] contentsBytes, String imageId, String validateSingnStatus, String billType, String md5, String ps) {
		this.name = name;
		this.type = type;
		this.imageId = imageId;
		this.validateSingnStatus = validateSingnStatus;
		this.billType = billType;
		this.md5 = md5;
		this.ps = ps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getValidateSingnStatus() {
		return validateSingnStatus;
	}

	public void setValidateSingnStatus(String validateSingnStatus) {
		this.validateSingnStatus = validateSingnStatus;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public String getBillCode() {
		return billCode;
	}

	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	@Override
	public String toString() {
		return "FilesInput{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", imageId='" + imageId + '\'' +
				", validateSingnStatus='" + validateSingnStatus + '\'' +
				", billType='" + billType + '\'' +
				", md5='" + md5 + '\'' +
				", ps='" + ps + '\'' +
				", billCode='" + billCode + '\'' +
				", billNumber='" + billNumber + '\'' +
				'}';
	}
}
