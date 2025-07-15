package com.st.modules.file.ftp.client.ftp.deprecared;

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


    /**
     * <pre>
     *     - 获取FTP客户端连接（自动登录与目录切换）
     *     - 使用 Apache Commons Net 库实现底层通信；
     *     - 基于 FTP 协议的文件存在性检查; 默认使用 FTP 协议，如需支持 FTPS（FTP over SSL）请改用 FTPSClient
     *     - 当前模式: 主动模式
     *     - 待优化: 使用被动模式，解决部分网络环境下连接问题;ftpClient.enterLocalPassiveMode() 可以解决部分防火墙阻断问题
     * </pre>
     * @return
     * @throws IOException
     */
    public static FTPClient getFtpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(remotePath);
        return ftpClient;
    }
}
