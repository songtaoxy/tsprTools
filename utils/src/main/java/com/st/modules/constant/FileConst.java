package com.st.modules.constant;

import com.st.modules.time.TimeUtils;

import java.io.File;

public class FileConst {

    public static final String userDir = "user.dir";
    public static final String appDir = System.getProperty(userDir);
    public static final String verticalLine ="|";

    public static final String filePostFixTxt = ".txt";
    public static final String filePostFixTarGz = ".tar.gz";

    public static final String s=File.separator;
    public static final String timeFlag = TimeUtils.time2StrCust("yyyy-MM-dd_HHmmss");

    // 凭证: 基本目录
    public static final String voucherTempDir = appDir+ File.separator+"voucher_temp"+File.separator;
    // 模块
    //- [[经费总账系统 fgls]] -〉Fund General Ledger System
    //- [[固定资产系统 fams ]]-> Fixed Assets Management System
    public static final String fgls = "fgls";
    public static final String fams = "fams";

    // 经费总账/下发文件目录
    public static final String fglsDistributeDir = voucherTempDir+fgls+s+"distribute"+s+timeFlag+s;
//    public static final String fglsDistributeDir = voucherTempDir+fgls+s+"distribute"+s;

    // 经费总账/接收文件目录
    public static final String fglsReceiveDir = voucherTempDir+fgls+s+"receive"+s+timeFlag+s;

    // 固定资产/下发文件目录
    public static final String famsDistributeDir = voucherTempDir+fgls+s+"distribute"+s+timeFlag+s;
    // 固定资产/接收文件目录
    public static final String famsReceiveDir = voucherTempDir+fgls+s+"receive"+s+timeFlag+s;

}
