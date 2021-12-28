package com.st.utils.io;

/**
 * @author: st
 * @date: 2021/12/29 02:37
 * @version: 1.0
 * @description:
 */

import com.st.utils.log.LogUtils;

import java.io.File;

/**
 * * Use multiple (more than 3) threads (you can choose to use thread pool) to copy a large file on
 * Disk D (find a file larger than 500M) to Disk E. Use RandomAccessFile to achieve.<p>
 *
 * After the
 * last thread ends, the file path of the copied file will be displayed! And prompted that the copy
 * is complete!<p></p>
 *
 * <a href="https://blog.krybot.com/a?ID=00550-60ee3250-5714-42d2-b113-177c7076caa4">主要参考</a>
 */
public class Main {
	public static void main(String[] args) {
		File from = new File("/Users/songtao/personaldriveMac/baklist/siyuan_datas_backup_20211227171000.tar.gz");
		File to = new File("/Users/songtao/downloads/");


		FileDownloadUtils.downLoad(from, to, new FileDownloadUtils.OnDownloadListener() {
			long startTime = System.currentTimeMillis();

			public void onComplete(File file) {
				System.out.println("Copy complete!");
				System.out.println(file);
				LogUtils.foal((System.currentTimeMillis() - startTime)+" milis","cost");
			}

			public void onProgress(int progress) {
				System.out.println(progress + "%");
			}

		});


	}
}
