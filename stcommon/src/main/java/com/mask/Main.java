package com.mask;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author: st
 * @date: 2022/7/8 14:19
 * @version: 1.0
 * @description:
 */

@Slf4j
public class Main {

	public static void main(String[] args) {

		String fileFullName = "/Users/songtao/downloads/temp/test.txt";

		updateFile(fileFullName);
	}

	/**
	 * 判断目录是否存在, 不存在, 则创建
	 *
	 * @param dir 目录(而非文件)
	 */
	public static void mkdir(String dir) {
		File folder = new File(dir);
		if (!folder.exists() && !folder.isDirectory()) {
			folder.mkdirs();
			log.info("目录已被创建:["+dir+"]");
		} else {
			log.info("目录已存在:["+dir+"]");
		}
	}


	/**
	 * 文件若存在, 则不做操作
	 *
	 * @param fileFullName
	 */
	@SneakyThrows
	public static void createFile(String fileFullName) {
		File file = new File(fileFullName);

		mkdir(file.getParent());

		if (!file.exists()) {
			file.createNewFile();
			log.info("文件已被创建:["+fileFullName+"]");
		} else {
			log.info("文件已存在:["+fileFullName+"]");
		}
	}

	/**
	 * 创建文件: 若文件存在, 则先删除, 后创建
	 * @param fileFullName
	 */
	@SneakyThrows
	public static void updateFile(String fileFullName) {
		File file = new File(fileFullName);

		mkdir(file.getParent());

		if (file.exists()) {
			log.info("文件已存在, 正在删除旧文件.... ");
			file.delete();
		}
		file.createNewFile();
		log.info("文件已被创建:["+fileFullName+"]");
	}
}
