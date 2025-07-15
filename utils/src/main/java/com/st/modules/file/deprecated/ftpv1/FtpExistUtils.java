package com.st.modules.file.deprecated.ftpv1;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
public class FtpExistUtils {

        /**
         * 目标
         * <pre>
         *  - 判断指定远程路径上的文件是否存在
         * </pre>
         * 功能
         * <pre>
         * - 基于 FTP 协议的文件存在性检查; 默认使用 FTP 协议，如需支持 FTPS（FTP over SSL）请改用 FTPSClient
         * - 文件夹不存在时 changeWorkingDirectory 会返回 false，直接判断文件不存在
         * - 支持指定完整远程路径进行检测；
         * - 使用 Apache Commons Net 库实现底层通信；
         * - 自动处理连接、登录、目录切换与断开等流程
         * </pre>
         * 注意事项
         * <pre>
         * - 路径分隔符： FTP 路径使用 /，不要使用 File.separator；
         * - 路径格式： remotePath 必须是完整路径（如 /logs/app.log）；
         * - 目录权限： 如果没有权限进入目标目录，则会返回不存在；
         * - 连接模式： 使用了 enterLocalPassiveMode()，防止主动模式被防火墙拦截；
         * - 异常处理： 所有网络和登录错误均抛出 IOException，调用方需捕获；
         * - 多次复用： 建议每次使用一个 FtpUtil 实例，线程安全性更高；
         * - 文件夹是否存在判断： 本方法只检测文件，不检测目录。
         * </pre>
         * Usage
         * <pre>
         * {@code
         * boolean exists = ftpUtil.fileExists("/data/report.csv");
         * System.out.println("文件是否存在: " + exists);
         * }
         * </pre>
         * @param remotePath 远程完整路径（如 /data/test.txt），必须使用正斜杠分隔
         * @return true 表示文件存在，false 表示不存在
         * @throws IOException 登录失败或网络异常时抛出
         */
        public boolean fileExists(String remotePath) throws IOException {
            FTPClient ftpClient = FTPClientFactory.getFtpClient();
            try {
                // 使用被动模式，解决部分网络环境下连接问题;ftpClient.enterLocalPassiveMode() 可以解决部分防火墙阻断问题
                // ftpClient.enterLocalPassiveMode();

                // 提取目录与文件名
                String dir = extractDir(remotePath);
                String filename = extractFilename(remotePath);

                // 切换到目标目录
                boolean changed = ftpClient.changeWorkingDirectory(dir);
                if (!changed) {
                    return false; // 目录不存在，文件一定不存在
                }

                // 列出该目录下是否存在指定文件
                FTPFile[] files = ftpClient.listFiles(filename);
                for (FTPFile file : files) {
                    if (file.isFile() && file.getName().equals(filename)) {
                        return true;
                    }
                }
                return false;
            } finally {
                FtpClientExitUtils.closeQuietly(ftpClient);
            }
        }


    /**
     * 功能
     * <pre>判断远程 FTP 上某个目录是否存在</pre>
     * Usage
     * <pre>
     *     {@code
     *     boolean dirExists = ftpUtil.directoryExists("/data/logs");
     *     System.out.println("目录是否存在: " + dirExists);
     *     }
     * </pre>
     * @param remoteDir 目录路径（如 /data/report），必须使用正斜杠
     * @return true 表示目录存在
     */
    public boolean directoryExists(String remoteDir) throws IOException {
        FTPClient ftpClient = FTPClientFactory.getFtpClient();
        try {
            // changeWorkingDirectory 会尝试切换目录，成功返回 true，失败返回 false
            return ftpClient.changeWorkingDirectory(remoteDir);
        } finally {
            FtpClientExitUtils.closeQuietly(ftpClient);
        }
    }

        /**
         * 提取路径中的目录部分，例如 /a/b/c.txt → /a/b
         */
        private String extractDir(String path) {
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash <= 0) {
                return "/";
            }
            return path.substring(0, lastSlash);
        }

        /**
         * 提取路径中的文件名部分，例如 /a/b/c.txt → c.txt
         */
        private String extractFilename(String path) {
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash == -1) {
                return path;
            }
            return path.substring(lastSlash + 1);
        }

    }

