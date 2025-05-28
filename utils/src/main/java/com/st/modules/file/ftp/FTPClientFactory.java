package com.st.modules.file.ftp;

import com.st.modules.config.DynamicAppConfig;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class FTPClientFactory {

    private static String server;
    private static int port;
    private static String user;
    private static String pass;
    private static String remotePath;

    // 静态块：初始化配置
    static {
        server = DynamicAppConfig.get("ftp.server");
        port = Integer.parseInt(DynamicAppConfig.get("ftp.port", "21"));
        user = DynamicAppConfig.get("ftp.user");
        pass = DynamicAppConfig.get("ftp.pass");
        remotePath = DynamicAppConfig.get("ftp.remotePath", "/");
    }


    // 获取FTP客户端连接（自动登录与目录切换）
    public static FTPClient getFtpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.setFileType(org.apache.commons.net.ftp.FTPClient.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(remotePath);
        return ftpClient;
    }
}
