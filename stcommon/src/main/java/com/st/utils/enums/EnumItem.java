package com.st.utils.enums;

import java.io.Serializable;

public interface EnumItem<V> extends Serializable {

	//泛型
	V getCode();

	String getValue();

	String getName();
}
