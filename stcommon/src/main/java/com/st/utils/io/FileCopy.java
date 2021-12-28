package com.st.utils.io;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: st
 * @date: 2021/12/29 01:53
 * @version: 1.0
 * @description:
 */
public class FileCopy implements Runnable{

	@Override
	public synchronized  void run() {
		InputStream is;
		try {
			is = new FileInputStream("/Users/songtao/personaldriveMac/baklist/siyuan_datas_backup_20211227171000.tar.gz");
			Reader rd=new InputStreamReader(is,"gbk");
			BufferedReader br=new BufferedReader(rd);
			OutputStream os=
					new FileOutputStream("/Users/songtao/downloads/xx.tar.gz");
			OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
			String str="";
			while((str=br.readLine())!=null){
				osw.write(str);
				//System.out.println(str);
			}
			osw.close();
			br.close();
			os.close();
			rd.close();
			is.close();
			System.out.println(Thread.currentThread().getName()+"：复制完毕");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		FileCopy cf=new FileCopy();
		ExecutorService es= Executors.newFixedThreadPool(3);
		es.execute(cf);
		es.execute(cf);
		es.execute(cf);
		es.shutdown();
	}
}