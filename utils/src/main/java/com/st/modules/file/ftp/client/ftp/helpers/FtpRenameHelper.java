package com.st.modules.file.ftp.client.ftp.helpers;

import com.st.modules.file.ftp.client.ftp.deprecared.FTPClientFactory;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.Objects;

public class FtpRenameHelper {

    /**
     * 将 FTP 上指定完整路径的文件重命名为新的完整路径
     *
     * @param oldPath 原始文件完整路径（如 /remote/path/old.txt）
     * @param newPath 新文件完整路径（如 /remote/path/new.txt）
     * @return true 表示重命名成功，false 表示失败
     * @throws IOException FTP 操作异常
     */
    public static boolean renameRemoteFile(FTPClient ftpClient, String oldPath, String newPath) throws IOException {
        Objects.requireNonNull(oldPath, "原路径不能为空");
        Objects.requireNonNull(newPath, "新路径不能为空");

//        FTPClient ftpClient = FTPClientFactory.getFtpClient();
        try {
            boolean flag = ftpClient.rename(oldPath, newPath);

            if (!flag) {
                throw new IOException("FTP 重命名失败: " + oldPath + " -> " + newPath);
            }

            return true;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException ignored) {
                    // 可选打印日志
                }
            }
        }
    }
    }


