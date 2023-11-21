package com.st.modules.enums.file;

import com.st.modules.enums.common.EnumItem;
import com.st.modules.formatter.Formatter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum FilePathEnum implements EnumItem<String> {

	E001("0000", "path.test", "testDir", null, null),
	E002("0000", "path.test.2", "testDir2", null, null);
	/*--------------code---------------*/
	private final String code;
	private final String name;
	private final String value;
	private final String extInfo;
	private final String des;

	FilePathEnum(String code, String name, String value, String extInfo, String des) {
		this.code = code;
		this.name = name;
		this.value = value;
		this.extInfo = extInfo;
		this.des = des;
	}


	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getExtInfo() {
		return extInfo;
	}

	@Override
	public String getDes() {
		return des;
	}


	public static Map<String, String> buildPathInfos(String baseDir, EnumItem e, String middleDir, String fileName) {
		HashMap<String, String> fileInfoMap = new HashMap<>();
		fileInfoMap.put("baseDir", baseDir);

		String fixedDir = e.getValue();
		fileInfoMap.put("fixedDir", fixedDir);

		fileInfoMap.put("middleDir", middleDir);
		fileInfoMap.put("fileName", fileName);

		String s = File.separator;
		String fileParenetDir = baseDir + s + fixedDir + s + middleDir + s;
		String filePath = fileParenetDir + fileName;
		fileInfoMap.put("fileParent", fileParenetDir);
		fileInfoMap.put("filePath", filePath);


		// optional log
		Formatter formatter = Formatter.init();
		formatter.setTopic("构建本地文件, 及相关信息");
		formatter.setObject(fileInfoMap);
		String format = formatter.format();
		System.out.println(format);

		return fileInfoMap;
	}

	public static String buildDir(String baseDir, EnumItem e, String middleDir, String fileName) {

		Map<String, String> fileInfoMap = buildPathInfos(baseDir, e, middleDir, fileName);

		String fileParent = fileInfoMap.get("fileParent");

		Formatter formatter = Formatter.init();
		formatter.setTopic("build local file infos/fileInfo/dir");
		formatter.setDes(fileParent);
		String format = formatter.format();
		System.out.println(format);

		return fileParent;


	}

	public static String buildPath(String baseDir, EnumItem e, String middleDir, String fileName) {

		Map<String, String> fileInfoMap = buildPathInfos(baseDir, e, middleDir, fileName);

		String filePath = fileInfoMap.get("filePath");

		Formatter formatter = Formatter.init();
		formatter.setTopic("build local file infos/fileInfo/full path");
		formatter.setDes(filePath);
		String format = formatter.format();
		System.out.println(format);

		return filePath;

	}

	public static void main(String[] args) {
		String dir = buildDir("/a/b/", FilePathEnum.E002, "c/d", "x.md");
		String path = buildPath("/a/b/", FilePathEnum.E002, "c/d", "x.md");

	}

}
