package com.st.modules.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description Md5加密工具类
 * @Date 2022/4/11 10:58
 **/
public class Md5Utils {

    private Md5Utils() {
    }

    /**
     * 全局数组
     **/
    private static final String[] strDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 返回形式为数字跟字符串
     *
     * @param bByte
     * @return
     */
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param bByte
     * @return
     */
    private static String byteToString(byte[] bByte) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < bByte.length; i++) {
            sBuilder.append(byteToArrayString(bByte[i]));
        }
        return sBuilder.toString();
    }

    /**
     * MD5加密
     *
     * @param str 待加密的字符串
     * @return
     */
    public static String enCoder(String str) {
        String result = null;
        try {
            result = str;
            MessageDigest md = MessageDigest.getInstance("MD5");
            result = byteToString(md.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
