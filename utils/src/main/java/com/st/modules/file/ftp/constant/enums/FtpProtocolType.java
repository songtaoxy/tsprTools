package com.st.modules.file.ftp.constant.enums;

public enum FtpProtocolType {
    FTP("ftp"),
    SFTP("sftp");

    private final String value;

    FtpProtocolType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FtpProtocolType from(String v) {
        for (FtpProtocolType t : values()) {
            if (t.value.equalsIgnoreCase(v)) {
                return t;
            }
        }
        throw new IllegalArgumentException("不支持的协议类型: " + v);
    }
}

