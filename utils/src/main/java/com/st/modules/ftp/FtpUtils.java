package com.st.modules.ftp;



import org.apache.commons.net.ftp.FTPClient;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * <pre>
 * - 安装步骤: pom.xml:commons-net的jar
 *
 * - ftp配置:
 * - - 配置可通过 resources/ftp.properties 管理,代码零硬编码
 *
 * - 多种上传方式: 字符串、文件、任意输入流
 * - 自动连接、登录、切换目录、异常处理
 * - 适合在实际开发中复用，便于单测和扩展
 *
 *
 * - 支持断点续传
 * - - FTP 断点续传：通过判断远程文件的已上传长度（ftpClient.size(filename)），本地流跳过对应字节，设置断点，再从断点续传
 * - - 前提：FTP 服务器与 commons-net 支持 REST 命令。
 *
 * - 上传方式:
 * - - 普通上传: uploadString,uploadFile, uploadStream
 * - - 断点上传: uploadStringC,uploadFileC, uploadStreamC
 *
 * - 下载: 普通下载, 断点下载
 *
 * 优化方向: 上传、下载, 使用多线程

 * </pre>
 */
public class FtpUtils {
    private static String server;
    private static int port;
    private static String user;
    private static String pass;
    private static String remotePath;

    // 静态块：初始化配置
    static {
        Properties props = new Properties();
        try (InputStream in = FtpUtils.class.getClassLoader().getResourceAsStream("ftp.properties")) {
            if (in != null) {
                props.load(in);
                server = props.getProperty("ftp.server");
                port = Integer.parseInt(props.getProperty("ftp.port", "21"));
                user = props.getProperty("ftp.user");
                pass = props.getProperty("ftp.pass");
                remotePath = props.getProperty("ftp.remotePath", "/");
            }
        } catch (Exception e) {
            throw new RuntimeException("FTP配置文件加载失败", e);
        }
    }

    // 获取FTP客户端连接（自动登录与目录切换）
    public static FTPClient getFtpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(remotePath);
        return ftpClient;
    }

    // 上传字符串内容为文件（如txt、csv等）
    public static boolean uploadString(String filename, String content) {
        try {FTPClient ftpClient = getFtpClient();
             InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            boolean done = ftpClient.storeFile(filename, input);
            return done;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 上传本地文件
    public static boolean uploadFile(String filename, File localFile) {
        try {FTPClient ftpClient = getFtpClient();
             InputStream input = new FileInputStream(localFile);
            boolean done = ftpClient.storeFile(filename, input);
            return done;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 上传输入流
    public static boolean uploadStream(String filename, InputStream in) {
        try {FTPClient ftpClient = getFtpClient();
             InputStream input = in ;
            boolean done = ftpClient.storeFile(filename, input);
            return done;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



        // 上传字符串内容为文件（支持断点续传）
        public static boolean uploadStringC(String filename, String content) {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            return uploadStreamC(filename, new ByteArrayInputStream(bytes), bytes.length);
        }

        // 上传本地文件（支持断点续传）
        public static boolean uploadFileC(String filename, File localFile) {
            try (InputStream in = new FileInputStream(localFile)) {
                return uploadStreamC(filename, in, localFile.length());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // 上传输入流（支持断点续传，需给出内容长度）
        public static boolean uploadStreamC(String filename, InputStream in, long totalLength) {
            FTPClient ftpClient = null;
            boolean success = false;
            try {
                ftpClient = getFtpClient();

                // 检查远程文件是否已存在，以及已上传长度
                long remoteSize = 0;
                try {
                    remoteSize = ftpClient.mlistFile(filename) != null ? ftpClient.mlistFile(filename).getSize() : 0;
                } catch (Exception ignore) { }
                // 若 remoteSize < 0，强制置0
                if (remoteSize < 0) remoteSize = 0;
                if (remoteSize > 0 && remoteSize < totalLength) {
                    // 断点续传：设置偏移量
                    ftpClient.setRestartOffset(remoteSize);
                    // 跳过已上传部分
                    in.skip(remoteSize);
                }

                // 覆盖上传（如未断点），或续传
                OutputStream os = (remoteSize > 0 && remoteSize < totalLength)
                        ? ftpClient.appendFileStream(filename)
                        : ftpClient.storeFileStream(filename);

                if (os == null) return false;

                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                os.close();

                // 需调用 completePendingCommand()，否则上传未结束
                success = ftpClient.completePendingCommand();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ftpClient != null && ftpClient.isConnected()) {
                    try { ftpClient.logout(); ftpClient.disconnect(); } catch (Exception ignored) {}
                }
                try { in.close(); } catch (Exception ignored) {}
            }
            return success;
        }

    // 假定 FtpUtils 中有 getFtpClient() 可用
    // 1. 普通下载：全量下载远程文件到本地

    /**
     * <pre>
     * - 直接覆盖写入
     * - case: boolean ok = FtpDownloadUtils.downloadFile("/upload/hello.txt", new File("/tmp/hello.txt"));
     * </pre>
     * @param remoteFile
     * @param localFile
     * @return
     */
    public static boolean downloadFile(String remoteFile, File localFile) {
        FTPClient ftpClient = null;
        try {
            ftpClient = FtpUtils.getFtpClient();
            try (OutputStream output = new BufferedOutputStream(new FileOutputStream(localFile))) {
                boolean success = ftpClient.retrieveFile(remoteFile, output);
                return success;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ftpClient != null && ftpClient.isConnected()) {
                try { ftpClient.logout(); ftpClient.disconnect(); } catch (Exception ignored) {}
            }
        }
    }

    // 2. 断点续传下载

    /**
     * <pre>
     * - 自动检测本地文件长度，设置 FTP 断点并续写（断点自动偏移、适合大文件断网重试）
     * - completePendingCommand(): 必须调用，确保 FTP 流操作完整提交
     * case: boolean resumed = FtpDownloadUtils.resumeDownload("/upload/bigfile.zip", new File("/tmp/bigfile.zip"));
     * </pre>
     * @param remoteFile
     * @param localFile
     * @return
     */
    public static boolean resumeDownload(String remoteFile, File localFile) {
        FTPClient ftpClient = null;
        boolean result = false;
        RandomAccessFile raf = null;
        InputStream is = null;
        try {
            ftpClient = FtpUtils.getFtpClient();

            // 获取远程文件大小
            long remoteSize = ftpClient.mlistFile(remoteFile) != null ? ftpClient.mlistFile(remoteFile).getSize() : 0;
            if (remoteSize <= 0) return false;

            // 获取本地文件大小
            long localSize = localFile.exists() ? localFile.length() : 0;

            // 如果本地文件大于等于远程文件，认为已完成
            if (localSize >= remoteSize) return true;

            // 设置断点（偏移）
            ftpClient.setRestartOffset(localSize);
            is = ftpClient.retrieveFileStream(remoteFile);

            raf = new RandomAccessFile(localFile, "rw");
            raf.seek(localSize);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                raf.write(buffer, 0, bytesRead);
            }

            // 必须调用 completePendingCommand()，否则续传未完成
            result = ftpClient.completePendingCommand();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (raf != null) try { raf.close(); } catch (Exception ignored) {}
            if (is != null) try { is.close(); } catch (Exception ignored) {}
            if (ftpClient != null && ftpClient.isConnected()) {
                try { ftpClient.logout(); ftpClient.disconnect(); } catch (Exception ignored) {}
            }
        }
        return result;
    }
    }


