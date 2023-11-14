package com.st.utils.bytes;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ByteUtilTest {

	@Test
	void intToByteArray() {
		byte[] bytes = ByteUtil.intToByteArray(300);
    // LogUtils.formatObjAndLogging(bytes,"");

    for (byte b : bytes) {
      System.out.println(b+"===>"+ByteUtil.byteToHex(b));
	}
	}

	@Test
	public void convert(){
		int ascii = 125;
		char ch1 = (char)ascii;
		System.out.println(ch1);


	}
}