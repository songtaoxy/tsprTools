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
public class FtpDownLoadHelper {

    /**
     * 根据文件名通配/后缀/关键字等，批量下载FTP文件
     * <pre>
     * 要点:
     * - 本地目录自动创建，避免手动 mkdir
     * - 支持任意复杂文件名筛选（Predicate<FTPFile>），最大灵活度
     * - 下载量/成功日志可控，便于自动化和监控
     * - 若需断点续传下载，建议在上述方法内调用 resumeDownload(...) 方法，见前述实现
     *
     * 线程并行优化建议
     * - 如果目录下文件量极大，可用 ExecutorService 并行多线程下载（每个文件一个线程任务）
     * - 推荐设定最大并发数，避免本地IO与FTP带宽瓶颈
     * </pre>
     *
     * Usage:
     * <pre>
     * {@code
     *
     * // 1. 以...结尾: 仅下载一个或多个tar.gz 文件
     * int n2 = FtpUtils.batchDownload("/remote/dir", "/tmp/localDir", f -> f.getName().endsWith(".tar.gz"));
     *
     * // 2. 下载整个目录: 批量下载目录下所有文件
     * int n1 = FtpUtils.batchDownloadDir("/remote/dir", "/tmp/localDir");
     *
     * // 3. 文件名包含关键字: 下载文件名包含"voucher"
     * int n3 = FtpUtils.batchDownloadByKeyword("/remote/dir", "/tmp/localDir", "voucher");
     *
     * // 4. 按正则（如下载以 2024 开头、以 .txt 结尾的文件）
     * int n4 = FtpUtils.batchDownloadByRegex("/remote/dir", "/tmp/localDir", "^2024.*\\.txt$");
     * }
     * </pre>
     * @param remoteDir
     * @param localDir
     * @param filter 文件名过滤（如f -> f.getName().endsWith(".tar.gz")）
     * @return 下载的文件(名)列表
     */
    public static  Map<String, List<String>>  batchDownload( FTPClient ftpClient, String remoteDir, String localDir, Predicate<FTPFile> filter) {
        Map<String, List<String>> fileMap = new HashMap<String, List<String>>();

        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();



//        FTPClient ftpClient = null;
        int successCount = 0;
        try {
//            ftpClient = FTPClientFactory.getFtpClient();
            ftpClient.changeWorkingDirectory(remoteDir);
            FTPFile[] files = ftpClient.listFiles();

            File localDirFile = new File(localDir);
            if (!localDirFile.exists()) localDirFile.mkdirs();

            for (FTPFile f : files) {
                String fileName = f.getName();

                if (!f.isFile()) continue;
                if (!filter.test(f)) continue;
                File localFile = new File(localDir, f.getName());
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile))) {
                    boolean ok = ftpClient.retrieveFile(f.getName(), out);
                    if (ok) {
                        System.out.println("下载成功：" + fileName);
                        successList.add(fileName);
                        successCount++;
                    } else {
                        System.out.println("下载失败：" + fileName);
                        failList.add(fileName);
                    }
                }
            }
            fileMap.put("successList", successList);
            fileMap.put("failList", failList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient != null && ftpClient.isConnected()) {
                try { ftpClient.logout(); ftpClient.disconnect(); } catch (Exception ignored) {}
            }
        }
        return fileMap;
    }


    /**
     * 批量下载远程目录下所有文件到本地目录
     * @param remoteDir FTP目录（如"/upload"）
     * @param localDir  本地目录（如"/tmp/download"），自动创建
     * @return 下载成功的文件数
     */
    public static Map<String, List<String>>  batchDownloadDir(FTPClient ftpClient,String remoteDir, String localDir) {
        return batchDownload(ftpClient, remoteDir, localDir, f -> true);
    }



    /**
     * 根据文件名正则批量下载
     * @param remoteDir FTP目录
     * @param localDir 本地目录
     * @param regex 正则表达式
     * @return 下载数
     */
    public static Map<String, List<String>>  batchDownloadByRegex(FTPClient ftpClient, String remoteDir, String localDir, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return batchDownload(ftpClient, remoteDir, localDir, f -> pattern.matcher(f.getName()).matches());
    }

    /**
     * 根据关键字批量下载
     * @param remoteDir
     * @param localDir
     * @param keyword 文件名含该关键字
     * @return 下载数
     */
    public static Map<String, List<String>>  batchDownloadByKeyword(FTPClient ftpClient, String remoteDir, String localDir, String keyword) {
        return batchDownload(ftpClient, remoteDir, localDir, f -> f.getName().contains(keyword));
    }

    }


