package com.st.practice.rex;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author: st
 * @date: 2023/1/12 12:40
 * @version: 1.0
 * @description:
 */
@Slf4j
public class Xthread implements Runnable{
	@Override
	public void run() {
		try {

			RexDemo.m();
		} catch (Exception e) {
			log.error(e.getMessage());
		}finally {

			log.error("---------------------------------");

		}
	}
}
