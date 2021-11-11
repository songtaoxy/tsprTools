package com.st.api.practice.cas;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: st
 * @date: 2021/7/10 13:48
 * @version: 1.0
 * @description:
 */
public class CASTest implements Serializable {

	public static void main(String[] args) {
		AtomicInteger atomicInteger = new AtomicInteger();
		int i = atomicInteger.incrementAndGet();
		System.out.println(i)	;
	}
}

