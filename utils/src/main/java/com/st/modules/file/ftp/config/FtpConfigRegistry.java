package com.st.modules.file.ftp.config;

import java.util.HashMap;
import java.util.Map;

public class FtpConfigRegistry {
    private static final Map<String, FtpClientConfig> CONFIGS = new HashMap<>();

    public static void init(Map<String, FtpClientConfig> configMap) {
        CONFIGS.clear();
        CONFIGS.putAll(configMap);
    }

    public static FtpClientConfig getClientConfig(String clientKey) {
        FtpClientConfig config = CONFIGS.get(clientKey);
        if (config == null) {
            throw new IllegalArgumentException("未找到FTP客户端配置: " + clientKey);
        }
        return config;
    }

    public static String getPath(String clientKey, String pathKey) {
        FtpClientConfig config = getClientConfig(clientKey);
        String path = config.getPaths().get(pathKey);
        if (path == null) {
            throw new IllegalArgumentException("未找到路径配置: " + pathKey + " in " + clientKey);
        }
        return path;
    }
}

