package com.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mask.FloodFillAlgorithm;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: st
 * @date: 2023/11/6 13:21
 * @version: 1.0
 * @description:
 */
public class FileList {

	public static void main(String[] args) {

		System.out.println("hi");

		String fileStrs = "{\n" +
				"  \"fileList\": [\n" +
				"    {\n" +
				"      \"name\": \"a.pdf\",\n" +
				"      \"type\": \"pdf\",\n" +
				"      \"contentsStr\": \"we are good\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"b.xml\",\n" +
				"      \"type\": \"xml\",\n" +
				"      \"contentsStr\": \"xxxxx\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"c.zip\",\n" +
				"      \"type\": \"zip\",\n" +
				"      \"contentsStr\": \"xxxxx\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		List<FilesInput> filesInputs = FileInputTools.parseFileStrs(fileStrs);
		for (FilesInput filesInput: filesInputs) {
			System.out.println(filesInput.toString());
		}
	}



}
