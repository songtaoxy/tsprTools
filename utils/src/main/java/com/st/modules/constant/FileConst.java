package com.st.modules.constant;

import com.st.modules.time.TimeUtils;

public class FileConst {

    public static final String appDir = System.getProperty("user.dir");
    public static final String verticalLine ="|";

    public static final String filePostFixTxt = ".txt";
    public static final String filePostFixTarGz = ".tar.gz";

    public static final String voucherTempFile = appDir+"/voucher_temp/";


    public static String buildVoucherTxt(String var){
        String path = null;
        String tempFilePath = voucherTempFile + var +filePostFixTxt;
        return path;
    }

    public static String buildVoucherTarGz(String var){
        String path = null;
        String tempFilePath = voucherTempFile + var +filePostFixTarGz;
        return path;
    }

}
