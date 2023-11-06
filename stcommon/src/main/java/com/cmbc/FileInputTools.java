package com.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	 * @param contentsStr
	 * @return
	 */
	public static byte[] b2s(String contentsStr) {

		byte[] bytes = null;
		bytes = contentsStr.getBytes(Charset.forName("ISO-8859-1"));

		return bytes;
	}

	/**
	 * 根据入参, 构建文件列表
	 * @param fileStrs
	 * @return
	 */
	public static List<FilesInput> parseFileStrs(String fileStrs) {

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
	}


	public static String buildJSonStr4(String inputs){
		System.out.println("call ");

		return null;
	}
}
