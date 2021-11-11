package com.st.utils.log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsLogsTest {

	@Test
	void formatObjAndLogging() {

		String o = "hi";
		String x = "";
		//x = null;
		//x = "this is my ps";
		UtilsLogs.formatObjAndLogging(o,x);
	}
}