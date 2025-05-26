package com.st.modules.file;


import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;

/**
 * @author: st
 * @date: 2023/11/14 15:56
 * @version: 1.0
 * @description:
 */
public class FileTrans {

	public static void main(String[] args) {
		String f1 = "/Users/songtao/personaldriveMac/Projects/tsprTools/stbptools008/src/main/java/com/st/modules/file/java-文件-转byte数组-转字符串-再转byte数组-再转文件.md";
		String f2 = "/Users/songtao/personaldriveMac/Projects/tsprTools/stbptools008/src/main/java/com/st/modules/file/java-文件-转byte数组-转字符串-再转byte数组-再转文件-2.md";
		String f3 = "/Users/songtao/personaldriveMac/Projects/tsprTools/stbptools008/src/main/java/com/st/modules/file/java-文件-转byte数组-转字符串-再转byte数组-再转文件-3.md";

		transFile_old(f1, f2);
		transFile(f1, f3);
	}

	/**
	 * <ul>功能
	 *     <li>1, 获取文件流 byte[]</li>
	 *     <li>2, 将文件流 byte[]->String </li>
	 *     <li>3, 发送String</li>
	 *     <li>4, 接收String</li>
	 *     <li>5, 将String转成byte[]</li>
	 *     <li>6, 将byte[] 写到文件</li>
	 * </ul>
	 * <ul>
	 *     应用场景
	 *     <li>网络传输文件</li>
	 *     <li>两个程序间传输文件</li>
	 * </ul>
	 * @param f1 文件的绝对路径
	 * @param f2 文件的绝对路径
	 */
	@SneakyThrows
	public static void transFile_old(String f1, String f2) {
		// 读取文件为字节数组
		File file = new File(f1);
		byte[] fileBytes = new byte[(int)file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(fileBytes);
		fis.close();

		// 字节数组转为Base64编码的String
		String encodedStr = Base64.getEncoder().encodeToString(fileBytes);

		// String转为字节数组
		byte[] decodedBytes = Base64.getDecoder().decode(encodedStr);

		// 字节数组写入新文件
		File newFile = new File(f2);
		FileOutputStream fos = new FileOutputStream(newFile);
		fos.write(decodedBytes);
		fos.close();

		System.out.println("文件转换完成");
	}



	/**
	 * <ul>功能
	 *     <li>1, 获取文件流 byte[]</li>
	 *     <li>2, 将文件流 byte[]->String </li>
	 *     <li>3, 发送String</li>
	 *     <li>4, 接收String</li>
	 *     <li>5, 将String转成byte[]</li>
	 *     <li>6, 将byte[] 写到文件</li>
	 * </ul>
	 * <ul>
	 *     应用场景
	 *     <li>网络传输文件</li>
	 *     <li>两个程序间传输文件</li>
	 * </ul>
	 * @param f1 文件的绝对路径
	 * @param f2 文件的绝对路径
	 */
	@SneakyThrows
	public static void  transFile(String f1,String f2) {
		// 读取文件为字节数组
		byte[] bytes = readFile(f1);

		// 字节数组转为Base64编码的String
		String fileStrEncoded = encodeBytes(bytes);

		// String转为字节数组
		byte[] fileBytesDecoded = decodeBytes(fileStrEncoded);

		// 字节数组写入新文件
		writeBytes2File(fileBytesDecoded,f2);

		System.out.println("文件转换完成");
	}

	@SneakyThrows
	public static byte[] readFile(String f) {
		// 读取文件为字节数组
		File file = new File(f);
		byte[] fileBytes = new byte[(int)file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(fileBytes);
		fis.close();
		return fileBytes;

	}

	@SneakyThrows
	public static String encodeBytes(byte[] fileBytes) {
		// 字节数组转为Base64编码的String
		String encodedStr = Base64.getEncoder().encodeToString(fileBytes);
		return encodedStr;
	}

	@SneakyThrows
	public static byte[] decodeBytes(String bytesStr) {
		// String转为字节数组
		byte[] decodedBytes = Base64.getDecoder().decode(bytesStr);
		return decodedBytes;
	}


	@SneakyThrows
	public static void  writeBytes2File(byte[] fileBytes,String filePath) {
		// 字节数组写入新文件
		File newFile = new File(filePath);
		FileOutputStream fos = new FileOutputStream(newFile);
		fos.write(fileBytes);
		fos.close();
	}


}

