package com.st.api.practice.encrypt.md5;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author: st
 * @date: 2021/6/30 00:08
 * @version: 1.0
 * @description:
 */
@Slf4j
public class MD5 {


	@Test
	public void md5(){
    String str = "this is good";
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		String s = DigestUtils.md5DigestAsHex(bytes);
		log.info(s);


		String replace = UUID.randomUUID().toString().replace("-", "");
		log.info(replace);


	}



}
