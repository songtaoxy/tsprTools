package com.st.modules.file.deprecated.ftpv1;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class FtpClientExitUtils {

    private FtpClientExitUtils() {
        // 工具类，禁止实例化
    }

    /**
     *
     * 概述
     * <pre>
     * - 安全关闭 FTP 连接;
     * - 将 closeQuietly 方法独立封装为一个工具类 FtpClientHelper 的实现方案，提升职责分离和复用性，便于在多个 FTP 操作类中共享使用
     * </pre>
     *
     * 优点
     * <pre>
     * - 单一职责	工具类只负责连接关闭逻辑
     * - 复用性高	多个组件共用，避免复制粘贴
     * - 更易测试	日后可在内部加入连接关闭统计、Mock 等操作
     * - 避免冗余	减少多个 FTP 操作类的重复代码
     * </pre>
     * Usage
     * <pre>
     *     {@code
     *     public boolean fileExists(String remotePath) throws IOException {
     *     FTPClient ftpClient = new FTPClient();
     *     try {
     *         ftpClient.connect(host, port);
     *         if (!ftpClient.login(username, password)) {
     *             throw new IOException("FTP 登录失败");
     *         }
     *         ftpClient.enterLocalPassiveMode();
     *
     *         String dir = extractDir(remotePath);
     *         String filename = extractFilename(remotePath);
     *
     *         if (!ftpClient.changeWorkingDirectory(dir)) {
     *             return false;
     *         }
     *
     *         return ftpClient.listFiles(filename).length > 0;
     *     } finally {
     *         FtpClientExitUtils.closeQuietly(ftpClient);
     *     }
     * }
     *     }
     * </pre>
     * @param ftpClient 已连接的 FTPClient 实例
     */
    public static void closeQuietly(FTPClient ftpClient) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            return;
        }
        try {
            ftpClient.logout();
        } catch (IOException ignored) {
        }
        try {
            ftpClient.disconnect();
        } catch (IOException ignored) {
        }
    }
}
