package com.st.practice.exception;

import javax.sound.midi.Soundbank;

/**
 * @author: st
 * @date: 2023/2/8 12:57
 * @version: 1.0
 * @description:
 */
public class E14_循环中出现异常 {

	public static void main(String[] args) {
		ma();
	}

	public static void   ma(){

		Integer time = 10;

		for (int i = 0; i < time; i++) {

			if (i == 5) {
				int x = 1 / 0;
			}
			System.out.println(i);


		}
	}
}
