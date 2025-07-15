package com.st.modules.file.ftp.client.ftp.helpers;



import com.st.modules.file.ftp.client.ftp.deprecared.FTPClientFactory;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * <pre>
 * - 安装步骤: pom.xml:commons-net的jar
 *
 * - ftp配置:
 * - - 配置可通过 {@code resources/ftp.properties} 管理,代码零硬编码
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
 *
 * dependencys
 * <pre>
 *  {@code
 *        <dependency>
 *             <groupId>commons-net</groupId>
 *             <artifactId>commons-net</artifactId>
 *             <version>3.8.0</version>
 *         </dependency>
 *  }
 * </pre>
 *
 * unit test ref {@code com.st.modules.ftp.FtpUtilsTest}
 */
public class FtpUploadResumeUtils {


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
                ftpClient = FTPClientFactory.getFtpClient();

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




    }


