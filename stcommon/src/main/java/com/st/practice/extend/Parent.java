package com.st.practice.extend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author: st
 * @date: 2022/4/20 17:17
 * @version: 1.0
 * @description:
 */

@Builder
@Data
@AllArgsConstructor
public class Parent {
	String name;

	public void  mParent() {

	}

}


