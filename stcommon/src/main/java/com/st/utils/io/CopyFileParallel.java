package com.st.utils.io;

import com.st.utils.log.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: st
 * @date: 2021/12/29 10:42
 * @version: 1.0
 * @description:
 */

/**
 * 复制单个文件, 多线程. <p></p>
 *
 * Use multiple (more than 3) threads (you can choose to use thread pool) to copy a large file on
 * Disk D (find a file larger than 500M) to Disk E. Use RandomAccessFile to achieve.<p>
 *
 * After the
 * last thread ends, the file path of the copied file will be displayed! And prompted that the copy
 * is complete!<p></p>
 *
 * <a href="https://blog.krybot.com/a?ID=00550-60ee3250-5714-42d2-b113-177c7076caa4">主要参考</a>
 */
public class CopyFileParallel {

	/**
	 * Creating main class for a test.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		File from = new File("/Users/songtao/personaldriveMac/baklist/siyuan_datas_backup_20211227171000.tar.gz");
		File to = new File("/Users/songtao/downloads/");


		FileDownloadUtils.downLoad(from, to, new FileDownloadUtils.OnDownloadListener() {
			long startTime = System.currentTimeMillis();

			public void onComplete(File file) {
				System.out.println("Copy complete!");
				System.out.println(file);
				//LogUtils.foal((System.currentTimeMillis() - startTime)+" milis","cost");
			}

			public void onProgress(int progress) {
				System.out.println(progress + "%");
			}

		});


	}

}



/**
 * 复制文件, 多线程. <p></p>
 *
 * Use multiple (more than 3) threads (you can choose to use thread pool) to copy a large file on
 * Disk D (find a file larger than 500M) to Disk E. Use RandomAccessFile to achieve.<p>
 *
 * After the
 * last thread ends, the file path of the copied file will be displayed! And prompted that the copy
 * is complete!<p></p>
 *
 * 要根据文件的大小, 设定线程数以及下面的缓冲区,否则线程过多, 线程切换耗时, 反而慢:
 * <pre>
 * 线程数: {@code private static final int LOAD_NUM = 10}
 * 缓冲区: {@code byte[] b = new byte[102400]; // 100M 缓存区}
 * </pre>
 *
 * <a href="https://blog.krybot.com/a?ID=00550-60ee3250-5714-42d2-b113-177c7076caa4">主要参考</a>
 */
class FileDownloadUtils {
	// 要根据文件的大小, 设定线程数以及下面的缓冲区,
	private static final int LOAD_NUM = 10;//Number of threads downloading at the same time
	private static ThreadPoolExecutor executor;//Thread pool
	private static final int sums[];//Record the download progress of each thread
	private static OnDownloadListener listener = null;
	private static int lastProgress = 0;//Record the last progress percentage
	private static int index = 0;
	static {
		sums = new int[LOAD_NUM];
		//Create thread pool
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(LOAD_NUM);
		executor.prestartAllCoreThreads();//preload thread
		//executor.setKeepAliveTime(1, TimeUnit.MICROSECONDS);
		//executor.allowCoreThreadTimeOut(true);
	}
	private static File dest;//Pasted target file
	private static long fileLength;//The total length of the copied file
	//url>>>D:/A/test.zip
	//dir>>>E:/B/E
	//dest>>E:/B/E/test.zip



	public static void downLoad(File url, File dir, OnDownloadListener l) {
		FileDownloadUtils.listener = l;//callback function
		final File src = url;
		fileLength = src.length();
		dest = new File(dir, src.getName());//The object described by the target file path
		setTemporaryFile(dest, src.length());//Open up temporary files
		long start = 0;
		long end = 0;
		for (int i = 0; i <LOAD_NUM; i++) {
			start = end;
			if (i == LOAD_NUM-1) {
				end = fileLength;
			} else {
				end = start + fileLength/LOAD_NUM;
			}
			LoadWorker loadWorker = new LoadWorker(src, dest, start, end, i);
			executor.execute(loadWorker);//Execute multiple threads and start downloading
		}

	}

	/**
	 * Complete the download.
	 */
	private static synchronized void loadComplete() {
		index++;
		if (index >= LOAD_NUM) {
			//The download is complete...
			if (listener != null) {
				listener.onComplete(dest);
				index = 0;
			}
		}
	}

	/**
	 * Standard protocol for monitoring functions.
	 *
	 */
	public interface OnDownloadListener {
		void onComplete(File file);//File download complete notification

		void onProgress(int progress);//Download progress notification
	}

	/**
	 * Set up temporary files
	 *
	 * @param file
	 */
	private static void setTemporaryFile(File file, long length) {
		try (RandomAccessFile raf = new RandomAccessFile(file, "rw");) {
			raf.setLength(length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Progress calculation
	 */
	private static void progress() {
		//The progress has changed." .
		if (listener != null) {
			long sunLength = 0;
			for (int i: sums) {
				sunLength += i;
			}
			//current progress percentage
			int progress = (int) (100.0 * sunLength/fileLength);
			if (progress> lastProgress) {//When the percentage point changes, notify the interface to call back
				//Prevent multiple thread colleagues from refreshing the same percentage point
				synchronized (FileDownloadUtils.class) {
					//Two-step verification
					if (progress> lastProgress) {//notify progress
						listener.onProgress(progress);//record the last percentage point
						lastProgress = progress;
					}
				}
			}
		}
	}

	private static class LoadWorker implements Runnable {
		private File src;//copied files
		private File dest;//Pasted file
		private long start;//The location where the current thread starts download
		private long end;//The position where the current thread ends the download
		private int index = 0;//Record the thread number, in order to record the progress

		public LoadWorker(File src, File dest, long start, long end, int index) {
			super();
			this.src = src;
			this.dest = dest;
			this.start = start;
			this.end = end;
			this.index = index;
		}

		@Override
		public void run() {
			try (RandomAccessFile from = new RandomAccessFile(src, "r");
				 RandomAccessFile to = new RandomAccessFile(dest, "rw");) {

				from.seek(start);
				to.seek(start);
				byte[] b = new byte[102400]; // 100M 缓存区
				int len = b.length;
				int sum = 0;
				while (true) {
					long poor = (end-start)-sum;
					if (poor <b.length) {
						len = (int) poor;
					}
					int read = from.read(b, 0, len);
					to.write(b, 0, read);
					sum += read;
					sums[index] = sum;//Record the download progress of the current thread
					progress();//Update progress
					if (sum >= end-start) {
						//The current thread has finished reading
						loadComplete();
						break;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
