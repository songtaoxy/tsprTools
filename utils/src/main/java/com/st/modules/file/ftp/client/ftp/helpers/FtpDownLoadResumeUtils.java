
package com.st.modules.file.ftp.client.ftp.helpers;



import com.st.modules.file.ftp.client.ftp.deprecared.FTPClientFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

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
public class FtpDownLoadResumeUtils {



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
            ftpClient = FTPClientFactory.getFtpClient();

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



