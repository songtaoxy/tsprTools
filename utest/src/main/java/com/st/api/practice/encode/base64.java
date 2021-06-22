package com.st.api.practice.encode;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author: st
 * @date: 2021/6/16 11:22
 * @version: 1.0
 * @description:
 */
public class base64 {
	public static void main(String[] args) throws UnsupportedEncodingException {
		String sqlStatment = "c2VsZWN0IGlkLHByb2R1Y3Qsb3JnLGF2YWlsYWJsZXF0eSxjdXJyZW50cXR5CmZyb20gc3RvY2suY3VycmVudHN0b2NrLkN1cnJlbnRTdG9ja1ZpZXcgCndoZXJlIGN1cnJlbnRxdHk+cGFyYW0kKGN1cnJlbnRxdHkpCmxpbWl0IDEwMA==";




		String realSql = new String(Base64.getDecoder().decode(sqlStatment), "UTF-8");

		System.out.println(realSql);
	}
}
