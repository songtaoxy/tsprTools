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
	 *
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
	 *
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

		JSONObject js = new JSONObject();
		for (int i = 0; i < 1; i++) {
			JSONObject jst = new JSONObject();
			jst.put("name", "name");

		}

		return null;
	}

	public static JSONObject o2j(FilesInput filesInput) {
		filesInput.setImageId("xxxxxxxxxxxxxxxxxxxxxx");
		JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(filesInput));
		return jsonObject;
	}
}
