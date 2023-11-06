package com.cmbc;

import java.nio.charset.Charset;
import java.util.Arrays;

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
public class FilesInput {
	/**
	 * 文件名称
	 */
	private String name;
	/**
	 * 文件类型: pdf, ofd, zip, xml
	 */
	private String type;
	/**
	 * 文件内容: oa传入 byte[]->string
	 */
	private String contentsStr;
	/**
	 * 文件内容: oa传入 byte[]->string->byte[]
	 */
	private byte[] contentsBytes;
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
	private String ps;



	public FilesInput() {
	}
	public FilesInput(String name, String type, String contentsStr) {
		this.name = name;
		this.type = type;
		this.contentsStr = contentsStr;
	}

	public String getContentsStr() {
		return contentsStr;
	}

	public void setContentsStr(String contentsStr) {
		this.contentsStr = contentsStr;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getContentsBytes() {
		return contentsBytes;
	}

	public void setContentsBytes(byte[] contentsBytes) {
		this.contentsBytes = contentsBytes;
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

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}


	@Override
	public String toString() {
		return "FilesInput{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", contentsStr='" + contentsStr + '\'' +
				", contentsBytes=" + Arrays.toString(contentsBytes) +
				", imageId='" + imageId + '\'' +
				", validateSingnStatus='" + validateSingnStatus + '\'' +
				", billType='" + billType + '\'' +
				", ps='" + ps + '\'' +
				'}';
	}
}
