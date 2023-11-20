package com.st.modules.enums.common;

import java.io.Serializable;

public interface EnumItem<V> extends Serializable {

	//泛型
	V getCode();
	String  getName();
	String getKey();
	String getValue();
	String getDes();
}
