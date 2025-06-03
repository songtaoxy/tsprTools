package com.st.modules.file.ftp;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.Objects;

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





    /**
     * 将 FTP 上指定完整路径的文件重命名为新的完整路径
     *
     * @param oldPath 原始文件完整路径（如 /remote/path/old.txt）
     * @param newPath 新文件完整路径（如 /remote/path/new.txt）
     * @return true 表示重命名成功，false 表示失败
     * @throws IOException FTP 操作异常
     */
    public static boolean renameRemoteFile(String oldPath, String newPath) throws IOException {
        Objects.requireNonNull(oldPath, "原路径不能为空");
        Objects.requireNonNull(newPath, "新路径不能为空");

        FTPClient ftpClient = FTPClientFactory.getFtpClient();
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


