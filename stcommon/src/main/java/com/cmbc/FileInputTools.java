package com.cmbc;


import com.cmbc.enums.FileTypeEnum;
import com.cmbc.tools.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: st
 * @date: 2023/11/6 14:24
 * @version: 1.0
 * @description:
 */
public class FileInputTools {
	/**
	 * 数据转换: byte[] -> String -> byte[]
	 *
	 * @param contentsStr
	 * @return
	 */
	public static byte[] b2s(String contentsStr) {

		byte[] bytes = null;
		bytes = contentsStr.getBytes(Charset.forName("ISO-8859-1"));

		return bytes;
	}

/*	*//**
	 * 根据入参, 构建文件列表
	 *
	 * @param fileStrs
	 * @return
	 *//*
	public static List<FilesInput> parseFileStrs2(String fileStrs) {

		List<FilesInput> filesInputList = new ArrayList<FilesInput>();

		JSONObject jsonObject = JSONObject.parseObject(fileStrs);
		JSONArray fileList = (JSONArray) jsonObject.get("fileList");
		for (int i = 0; i < fileList.size(); i++) {
			JSONObject obj = fileList.getJSONObject(i);
			String name = (String) obj.get("name");
			String type = (String) obj.get("type");
			String contentsStr = (String) obj.get("contentsStr");

			FilesInput filesInput = new FilesInput(name, type, contentsStr);
			filesInput.setContentsBytes(FileInputTools.b2s(contentsStr));
			filesInputList.add(filesInput);

		}
		return filesInputList;
	}*/


	/**
	 * 根据入参, 构建文件列表
	 *
	 * @param fileStrs
	 * @return
	 */
	public static List<FilesInput> parseFileStrs(String fileStrs) {

		JsonObject js = GsonUtils.o2j(fileStrs);
		JsonArray fileList = js.getAsJsonArray("fileList");
		List<FilesInput> filesInputs = GsonUtils.jsaStr2List(fileList.toString(), FilesInput.class);
		for (FilesInput filesInput : filesInputs) {
			filesInput.setContentsBytes(FileInputTools.b2s(filesInput.getContentsStr()));
		}
		return filesInputs;
	}


	public static FilesInput queryTargetObj(List<FilesInput> filesInputList) {

		FilesInput filesInput = null;
		String pdf = FileTypeEnum.PDF.getName();
		String ofd = FileTypeEnum.OFD.getName();

		for (FilesInput f : filesInputList) {
			String type = f.getType();
			if (pdf.equalsIgnoreCase(type) || ofd.equalsIgnoreCase(type)) {
				filesInput = f;
			}
		}
		return filesInput;
	}


	/**
	 * <li>调用报账系统进行报账时,需传递的文件信息</li>
	 * <ul>
	 *     <li>文件名</li>
	 *     <li>文件类型</li>
	 *     <li>文件imageID</li>
	 *     <li>文件验签状态</li>
	 * </ul>
	 *
	 * @param inputs
	 * @return
	 */
	public static String buildJSonStr4Reimburse(String inputs) {
		System.out.println("call ");

		for (int i = 0; i < 1; i++) {

		}
		return null;
	}

}
