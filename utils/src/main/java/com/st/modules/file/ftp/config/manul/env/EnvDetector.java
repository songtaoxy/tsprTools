package com.st.modules.file.ftp.config.manul.env;

public class EnvDetector {
    public static String getActiveEnv() {
        return System.getProperty("appEnv", "dev").toLowerCase();
    }
}
