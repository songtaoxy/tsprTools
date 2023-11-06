package com.cmbc;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author: st
 * @date: 2023/11/6 13:27
 * @version: 1.0
 * @description:
 */
public class FilesInput {
	private String name;
	private String type;
	private String contentsStr;
	private byte[] contentsBytes;

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


	@Override
	public String toString() {
		return "FilesInput{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", contentsStr='" + contentsStr + '\'' +
				", contentsBytes=" + Arrays.toString(contentsBytes) +
				'}';
	}
}
