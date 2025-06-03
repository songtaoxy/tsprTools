package com.st.modules.file.ftp;

import org.apache.commons.net.ftp.FTPClient;

public class FtpUtils {

    /**
     * 递归创建 FTP 远程目录（完整路径）
     * 如果某级目录已存在，则跳过创建，继续下一级
     * @param fullRemotePath 远程完整目录路径，如 "/a/b/c"
     * @throws Exception FTP连接或操作异常
     */
    public static void makeRemoteDirs(String fullRemotePath) throws Exception {
        FTPClient ftpClient = FTPClientFactory.getFtpClient();
        try {
            ftpClient.enterLocalPassiveMode(); // 推荐开启
            String[] dirs = fullRemotePath.split("/");
            String currentPath = "";
            for (String dir : dirs) {
                if (dir == null || dir.isEmpty()) continue;
                currentPath += "/" + dir;

                // 检查当前路径是否存在，不存在才创建
                if (!ftpClient.changeWorkingDirectory(currentPath)) {
                    boolean created = ftpClient.makeDirectory(currentPath);
                    if (!created) {
                        throw new RuntimeException("创建目录失败: " + currentPath);
                    }
                }
            }
        } finally {
            ftpClient.disconnect();
        }
    }
}
