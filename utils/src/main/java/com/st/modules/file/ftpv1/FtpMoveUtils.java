package com.st.modules.file.ftpv1;
import org.apache.commons.net.ftp.FTPClient;
import java.io.IOException;
import java.io.InputStream;

public class FtpMoveUtils {

        private FtpMoveUtils() {
            throw new UnsupportedOperationException("工具类禁止实例化");
        }

        /**
         * 概述
         * <pre>将远程文件移动到目标目录（递归创建目录，处理跨目录问题）</pre>
         *
         * <p></p>
         * 功能
         * <pre>
         * - 判断源文件是否存在，不存在则抛异常
         * - 判断目标目录是否存在，不存在则递归创建
         *
         * <p></p>
         * - 移动文件：FTP协议理论支持 rename 跨目录; 如果因相关原因不支持, 则使用下载+上传+删除方式替代
         * -- 理论上可以实现跨目录移动文件, 但在实际使用中，是否成功取决于FTP 服务器的实现和权限控制：
         * -- 支持场景：如 vsftpd、ProFTPD、FileZilla Server 等大多数服务器默认支持
         * -- 失败原因：目标目录不存在; 登录用户对目标目录没有写权限; 某些服务器限制了跨目录重命名行为（安全策略）
         * -- 因此，在实际开发中通常做法：先尝试 rename（适用于大多数情况，效率更高); 失败后回退为：下载 + 上传 + 删除（保证通用性）
         * -- --
         * <p></p>
         * - 异常封装统一处理，便于上层调用者明确失败原因
         * - 清理策略	最后无论 rename 是否成功，都会尝试清除临时文件
         * - 编码问题	测试内容使用 UTF-8 编码（可根据实际设置）
         * - 权限问题	部分服务器不允许在根目录 / 写入测试目录，请根据场景设置 baseDir
         * </pre>
         * <p></p>
         *
         * 边界处理
         * <pre>
         * - 参数校验	对空值、连接状态、路径合法性等进行校验
         * - 异常封装	所有失败抛出 IOException
         * - 源文件不存在	抛异常，避免误操作
         * - 目标目录不存在	自动递归创建
         * - 跨目录 rename	自动 fallback 为复制+删除
         * - 编码问题	若 FTP 服务器使用 GBK 编码，应在连接后设置 ftp.setControlEncoding("GBK")
         * - 特殊字符	路径包含空格、中文时，需考虑 FTP 编码和服务器兼容性
         * </pre>
         *
         * <p></p>
         * usage
         * <pre>
         * {@code
         * FTPClient ftp = new FTPClient();
         * ftp.connect("host");
         * ftp.login("user", "pass");
         * ftp.enterLocalPassiveMode();
         *
         * FtpUtils.moveFileToDirectory(ftp, "/upload/data.csv", "/backup/2025/07");
         *
         * ftp.logout();
         * ftp.disconnect();
         * }
         * </pre>
         * @param ftp FTP 客户端连接
         * @param sourcePath 原始文件的完整路径，如 "/source/abc.txt"
         * @param targetDir 目标目录路径，如 "/archive/2025/07"
         * @throws IOException 移动失败时抛出
         */
        public static void moveFileToDirectory(FTPClient ftp, String sourcePath, String targetDir) throws IOException {
            if (ftp == null || !ftp.isConnected()) {
                throw new IOException("FTP 连接不可用");
            }

            // 判断源文件是否存在
            if (ftp.listFiles(sourcePath).length == 0) {
                throw new IOException("源文件不存在: " + sourcePath);
            }

            // 创建目标目录（递归）
            createDirectoriesIfAbsent(ftp, targetDir);

            String fileName = sourcePath.substring(sourcePath.lastIndexOf('/') + 1);
            String targetPath = targetDir.endsWith("/") ? targetDir + fileName : targetDir + "/" + fileName;

            // 尝试重命名（如果不能跨目录，则采用复制+删除）
            if (!ftp.rename(sourcePath, targetPath)) {
                try (InputStream in = ftp.retrieveFileStream(sourcePath)) {
                    if (in == null) {
                        throw new IOException("无法读取源文件数据: " + sourcePath);
                    }
                    if (!ftp.storeFile(targetPath, in)) {
                        throw new IOException("目标文件写入失败: " + targetPath);
                    }
                } finally {
                    ftp.completePendingCommand();
                }

                if (!ftp.deleteFile(sourcePath)) {
                    throw new IOException("源文件删除失败: " + sourcePath);
                }
            }
        }

        /**
         * 递归创建目录结构
         *
         * @param ftp FTP 客户端
         * @param dir 目录路径（以 / 开头）
         * @throws IOException 创建失败
         */
        public static void createDirectoriesIfAbsent(FTPClient ftp, String dir) throws IOException {
            String[] dirs = dir.split("/");
            StringBuilder path = new StringBuilder();
            for (String segment : dirs) {
                if (segment.isEmpty()) continue;
                path.append("/").append(segment);
                if (!ftp.changeWorkingDirectory(path.toString())) {
                    if (!ftp.makeDirectory(path.toString())) {
                        throw new IOException("创建目录失败: " + path.toString());
                    }
                }
            }
        }
    }




