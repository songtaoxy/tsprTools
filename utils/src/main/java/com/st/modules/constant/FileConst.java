package com.st.modules.constant;

import com.st.modules.time.TimeUtils;

import java.io.File;

public class FileConst {

    public static final String appDir = System.getProperty("user.dir");
    public static final String verticalLine ="|";

    public static final String filePostFixTxt = ".txt";
    public static final String filePostFixTarGz = ".tar.gz";

    public static final String voucherTempFile = appDir+ File.separator+"voucher_temp"+File.separator+TimeUtils.time2StrCust("yyyy-MM-dd_HHmmss")+File.separator;



}
