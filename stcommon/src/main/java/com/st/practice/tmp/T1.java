package com.st.practice.tmp;

import java.io.File;

/**
 * @author: st
 * @date: 2023/1/13 17:08
 * @version: 1.0
 * @description:
 */
public class T1 {

	public static void main(String[] args) {
		//String path = "/Users/songtao/downloads/a/b/cd/x.md";
		String path = "/Users/songtao/downloads/a/b/";
		deleteDir(new File(path));
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
//递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}
}
