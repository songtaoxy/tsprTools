package com.st.modules.file.wp.mogo;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.st.modules.file.local.FileTypeEnum;
import com.st.modules.json.jackson.JacksonUtils;
import com.st.modules.log.LogBody;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: st
 * @date: 2023/11/6 14:24
 * @version: 1.0
 * @description:
 */
public class FileInputUitls {
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
			filesInput.setContentsBytes(FileInputUitls.b2s(contentsStr));
			filesInputList.add(filesInput);

		}
		return filesInputList;
	}


	/**
	 * 根据入参, 构建文件列表
	 *
	 * //@param fileStrs
	 * @return
	 */
/*	public static List<FilesInput> parseFileStrs(String fileStrs) {

		JsonObject js = GsonUtils.o2j(fileStrs);
		JsonArray fileList = js.getAsJsonArray("fileList");
		List<FilesInput> filesInputs = GsonUtils.jsaStr2List(fileList.toString(), FilesInput.class);
		for (FilesInput filesInput : filesInputs) {
			filesInput.setContentsBytes(FileInputTools.b2s(filesInput.getContentsStr()));
		}
		return filesInputs;
	}*/


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
	 * <li>查找目标类型文件, 对应的vo</li>
	 * <pre>
	 *     {@link
	 *     // FileInputTools#queryTargetObj(filesInputList, FileTypeEnum.PDF_OFD.getCode());
	 *     }
	 * </pre>
	 * @param filesInputList
	 * @param code
	 * @return
	 */
	public static FilesInput queryTargetObj(List<FilesInput> filesInputList, String code) {

		// optional log
		String topic = "/智能验票/oa上传发票/税管查询文件类型及目标文件";
		LogBody logBody = new LogBody();
		logBody.setTopic(topic);
//		JSONObject js = FastJsonUtil.buildJS();
		ObjectNode js  = JacksonUtils.createObjectNode();
		js.put("目标文件类型编码",code);

		 // return
		FilesInput filesInput = null;


		String pdf = FileTypeEnum.PDF.getName();
		String ofd = FileTypeEnum.OFD.getName();
		String zip = FileTypeEnum.ZIP.getName();
		String xml = FileTypeEnum.XML.getName();

		String pdfOfdCode = FileTypeEnum.PDF_OFD.getCode();
		String xmlZipCode = FileTypeEnum.XML_ZIP.getCode();

		for (FilesInput f : filesInputList) {
			String type = f.getType();

			if (xmlZipCode.equalsIgnoreCase(code)) {
				if (zip.equalsIgnoreCase(type) || xml.equalsIgnoreCase(type)) {
					filesInput = f;
					js.put("目标文件类型存在, 其类型是:",type);
				}
			} else if (pdfOfdCode.equalsIgnoreCase(code)) {
				if (pdf.equalsIgnoreCase(type) || ofd.equalsIgnoreCase(type)) {
					filesInput = f;
					js.put("目标文件类型存在, 其类型是:",type);
				}
			} else {
				filesInput = null;
				js.put("目标文件类型不存在, 其类型是:",FileTypeEnum.getEnumByCode(code).getName());
			}

		}

		// optional log
//		logBody.setInfos_js(js);
		logBody.setInfos_obj(js);
//		String format = FastJsonUtil.format(logBody);
		String format = JacksonUtils.toPrettyJson(logBody);
		System.out.println(format);

		return filesInput;
	}

}
