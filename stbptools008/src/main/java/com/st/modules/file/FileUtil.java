package com.st.modules.file;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.st.modules.log.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author: st
 * @date: 2023/2/6 11:39
 * @version: 1.0
 * @description:
 */
@Slf4j
public class FileUtil {

	public static void main(String[] args) {
		File file = new File("/Users/songtao/downloads/");
		//File file = new File("/Users/songtao/personaldriveMac/");
		double dirSize = getFileSize(file);
	}

	/**
	 * <li>获取文件或目录(整个目录)的大小</li>
	 *
	 * @param file
	 * @return 文件大小:单位 bytes
	 */
	public static Long getFileSize(File file) {

		Long fileSizeRecursive = getFileSizeRecursive(file);
		fileSizeHandler(file, fileSizeRecursive);

		return fileSizeRecursive;


	}

	//@SneakyThrows
	public static Long getFileSizeRecursive(File file) {

		//判断文件是否存在
		if (file.exists()) {
			if (file.isFile())
				return file.length();
			final File[] children = file.listFiles();
			long total = 0;
			if (children != null)
				for (final File child : children)
					total += getFileSizeRecursive(child);
			return total;
		} else {
			Preconditions.checkArgument(false, "file[" + file.getAbsolutePath() + "]不存在，请检查路径是否正确！");
			//return 0L;
		}
		return null;
	}

	/**
	 * <li>单位转换: 人容易识别的单位 如: M,G, ...</li>
	 *
	 * @param file
	 * @param size
	 * @param <T>
	 * @return
	 */
	public static <T> T fileSizeHandler(File file, Long size) {
		T t = null;


		String file_type = "file";
		boolean directory = file.isDirectory();
		if (directory) {
			file_type = "dir";
		}

		JsonObject js = new JsonObject();
		js.addProperty("fie_path", file.getAbsolutePath());
		js.addProperty("file_type", file_type);


		if (size <= 1000) {
			js.addProperty("file_size", size + " bytes");
		} else {
			if (size / 1000 <= 1000) {
				js.addProperty("file_size", size / 1024 + " KB");

			} else {
				if (size / 1000 / 1000 <= 1000) {
					js.addProperty("file_size", size / 1024 / 1024 + " M");

				} else {
					if (size / 1000 / 1000 / 1000 <= 1000) {
						js.addProperty("file_size", size / 1024 / 1024 / 1024 + " G");
					} else {

						js.addProperty("file_size", size / 1024 / 1024 / 1024 / 1024 + " T");
					}
				}
			}
		}
		log.info(js.toString());
		LogUtils.printGson("File Infos", js);
		return t;
	}


}
