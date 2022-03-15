package com.st.utils.base64;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author: st
 * @date: 2022/3/15 10:52
 * @version: 1.0
 * @description:
 */
public class Base64Demo {
  public static void main(String[] args) throws UnsupportedEncodingException {

    String str = "4ce5775189522c36c1b71d6f2a28ad6e";

    String base64encodedString = Base64.getEncoder().encodeToString(str.getBytes("utf-8"));

    //System.out.println("Base64 编码字符串 (基本) :" + base64encodedString);

    // 解码
    byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);

    String strdec = new String(base64decodedBytes, "utf-8");

    //System.out.println("原始字符串: " + new String(base64decodedBytes, "utf-8"));

    //System.out.println(str.equals(strdec));


	  String strDec = new String(Base64.getDecoder().decode(Base64.getEncoder().encodeToString(str.getBytes("utf-8"))), "utf-8");

    System.out.println(strDec);
	  System.out.println(strDec.equals(str));
  }
}
