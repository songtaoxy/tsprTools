package com.st.practice.nullconvert;

import com.google.common.base.Preconditions;

/**
 * @author: st
 * @date: 2022/3/22 20:11
 * @version: 1.0
 * @description:
 */
public class NullDemo2 {

	public static void precon() {

		String x = null;
		Preconditions.checkNotNull(x, "null is wrong");
	}
}
