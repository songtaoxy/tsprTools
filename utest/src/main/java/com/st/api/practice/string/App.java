package com.st.api.practice.string;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author: st
 * @date: 2021/10/11 20:37
 * @version: 1.0
 * @description:
 */
public class App {


		public static void main(String[] args) throws IOException {
			String hello = "param1_Arthas";
			while (true) {
				boolean contains = StringUtils.contains(hello, "Arthas");
				boolean containsx = StringUtils.contains(hello, "good");
				System.out.println(contains);
				System.out.println(containsx);

			}
		}
	}

