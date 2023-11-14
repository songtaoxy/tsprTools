package com.st.modules.bytes;

import java.io.UnsupportedEncodingException;

/**
 * @author: st
 * @date: 2021/11/26 13:36
 * @version: 1.0
 * @description:
 */
public class ByteUtil {

  /**
   * int到byte[] 由高位到低位
   * @param i 需要转换为byte数组的整行值。
   * @return byte数组
   */
  public static byte[] intToByteArray(int i) {
    byte[] result = new byte[4];
    result[0] = (byte)((i >> 24) & 0xFF);
    result[1] = (byte)((i >> 16) & 0xFF);
    result[2] = (byte)((i >> 8) & 0xFF);
    result[3] = (byte)(i & 0xFF);
    return result;
  }

  /**
   * byte[]转int
   * @param bytes 需要转换成int的数组
   * @return int值
   */
  public static int byteArrayToInt(byte[] bytes) {
    int value=0;
    for(int i = 0; i < 4; i++) {
      int shift= (3-i) * 8;
      value +=(bytes[i] & 0xFF) << shift;
    }
    return value;
  }

  // 将低字节在前转为int，高字节在后的byte数组(与IntToByteArray1想对应)
  public int ByteArrayToInt(byte[] bArr) {
    if (bArr.length != 4) {
      return -1;
    }
    return (int)
        ((((bArr[3] & 0xff) << 24)
            | ((bArr[2] & 0xff) << 16)
            | ((bArr[1] & 0xff) << 8)
            | ((bArr[0] & 0xff) << 0)));
}

  /**
   * 有符号的byte, 转无符号的int
   * @param b 输入的byte, [-128,127]
   * @return byte对应的无符号的int [0-255]
   */
  public static int byte2Int(byte b){
    //byte a = 129;
    return b & 0xff;
  }

  /**
   * 字节转十六进制
   *
   * @param b 需要进行转换的byte字节
   * @return 转换后的Hex字符串
   */
  public static String byteToHex(byte b) {
    String hex = Integer.toHexString(b & 0xFF);
    if (hex.length() < 2) {
      hex = "0" + hex;
    }
    return hex;
  }

  /**
   * 字节数组转16进制
   *
   * @param bytes 需要转换的byte数组
   * @return 转换后的Hex字符串
   */
  public static String bytesToHex(byte[] bytes) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < bytes.length; i++) {
      String hex = Integer.toHexString(bytes[i] & 0xFF);
      if (hex.length() < 2) {
        sb.append(0);
      }
      sb.append(hex);
    }
    return sb.toString();
  }

  /**
   * Hex字符串转byte <p></p>
   *
   * 需注意的是，Hex的字符串必须为十六进制的字符，否则会抛出异常。Hex的范围为0x00到0xFF。<p></p>
   *
   * @param inHex 待转换的Hex字符串
   * @return 转换后的byte
   */
  public static byte hexToByte(String inHex) {
    return (byte) Integer.parseInt(inHex, 16);
  }

  /**
   * hex字符串转byte数组<p></p>
   *
   * 如果Hex超过0xFF，显然转换后结果不是一个byte，而是一个byte数组<p>
   *
   * @param inHex 待转换的Hex字符串
   * @return 转换后的byte数组结果
   */
  public static byte[] hexToByteArray(String inHex) {
    int hexlen = inHex.length();
    byte[] result;
    if (hexlen % 2 == 1) {
      // 奇数
      hexlen++;
      result = new byte[(hexlen / 2)];
      inHex = "0" + inHex;
    } else {
      // 偶数
      result = new byte[(hexlen / 2)];
    }
    int j = 0;
    for (int i = 0; i < hexlen; i += 2) {
      result[j] = hexToByte(inHex.substring(i, i + 2));
      j++;
    }
    return result;
  }


  /**
   * 将byte数组转化成String,为了支持中文，转化时用GBK编码方式
   */
  public String ByteArraytoString(byte[] valArr,int maxLen) {
    String result=null;
    int index = 0;
    while(index < valArr.length && index < maxLen) {
      if(valArr[index] == 0) {
        break;
      }
      index++;
    }
    byte[] temp = new byte[index];
    System.arraycopy(valArr, 0, temp, 0, index);
    try {
      result= new String(temp,"GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result;
  }
  /**
   * 将String转化为byte,为了支持中文，转化时用GBK编码方式
   */
  public byte[] StringToByteArray(String str){
    byte[] temp = null;
    try {
      temp = str.getBytes("GBK");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return temp;
  }
}
