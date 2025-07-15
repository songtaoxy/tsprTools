package com.st.modules.file.ftpv1;



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
public class FtpDownLoadUtils {



    // 上传字符串内容为文件（如txt、csv等）
    public static boolean uploadString(String filename, String content) {
        try {FTPClient ftpClient = FTPClientFactory.getFtpClient();
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
        try {FTPClient ftpClient = FTPClientFactory.getFtpClient();
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
        try {FTPClient ftpClient = FTPClientFactory.getFtpClient();
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
            ftpClient = FTPClientFactory.getFtpClient();
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

    // ##########################################
    // part: 批量上传:上传目录或多个目标文件
    // ##########################################



    // ##########################################
    // part: 批量下载: 下载目录或多个目标文件
    // ##########################################

    /**
     * 批量下载远程目录下所有文件到本地目录
     * @param remoteDir FTP目录（如"/upload"）
     * @param localDir  本地目录（如"/tmp/download"），自动创建
     * @return 下载成功的文件数
     */
    public static int batchDownloadDir(String remoteDir, String localDir) {
        return batchDownload(remoteDir, localDir, f -> true);
    }

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
     * Usage
     * <pre>
     * {@code
     * // 1. 批量下载目录下所有文件
     * int n1 = FtpUtils.batchDownloadDir("/remote/dir", "/tmp/localDir");
     *
     * // 2. 仅下载 tar.gz 文件
     * int n2 = FtpUtils.batchDownload("/remote/dir", "/tmp/localDir", f -> f.getName().endsWith(".tar.gz"));
     *
     * // 3. 下载文件名包含"voucher"
     * int n3 = FtpUtils.batchDownloadByKeyword("/remote/dir", "/tmp/localDir", "voucher");
     *
     * // 4. 按正则（如下载以 2024 开头、以 .txt 结尾的文件）
     * int n4 = FtpUtils.batchDownloadByRegex("/remote/dir", "/tmp/localDir", "^2024.*\\.txt$");
     * }
     * </pre>
     * @param remoteDir FTP目录
     * @param localDir  本地目录
     * @param filter    文件名过滤（如f -> f.getName().endsWith(".tar.gz")）
     * @return 下载成功的文件数
     */
    public static int batchDownload(String remoteDir, String localDir, Predicate<FTPFile> filter) {
        FTPClient ftpClient = null;
        int successCount = 0;
        try {
            ftpClient = FTPClientFactory.getFtpClient();
            ftpClient.changeWorkingDirectory(remoteDir);
            FTPFile[] files = ftpClient.listFiles();

            File localDirFile = new File(localDir);
            if (!localDirFile.exists()) localDirFile.mkdirs();

            for (FTPFile f : files) {
                if (!f.isFile()) continue;
                if (!filter.test(f)) continue;
                File localFile = new File(localDir, f.getName());
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile))) {
                    boolean ok = ftpClient.retrieveFile(f.getName(), out);
                    if (ok) {
                        System.out.println("下载成功：" + f.getName());
                        successCount++;
                    } else {
                        System.out.println("下载失败：" + f.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient != null && ftpClient.isConnected()) {
                try { ftpClient.logout(); ftpClient.disconnect(); } catch (Exception ignored) {}
            }
        }
        return successCount;
    }


    /**
     * <pre>
     *     - 相关说明, see {@code com.st.modules.file.ftp.FtpDownLoadUtils#batchDownload(java.lang.String, java.lang.String, java.util.function.Predicate)}
     *     - 扩展点: 功能与batchDownload相同; 只是返回的不是int, 而是文件列表
     * </pre>
     * @param remoteDir
     * @param localDir
     * @param filter
     * @return
     */
    public static  Map<String, List<String>>  batchDownloadExt(String remoteDir, String localDir, Predicate<FTPFile> filter) {
        Map<String, List<String>> fileMap = new HashMap<String, List<String>>();

        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();



        FTPClient ftpClient = null;
        int successCount = 0;
        try {
            ftpClient = FTPClientFactory.getFtpClient();
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
     * 根据文件名正则批量下载
     * @param remoteDir FTP目录
     * @param localDir 本地目录
     * @param regex 正则表达式
     * @return 下载数
     */
    public static int batchDownloadByRegex(String remoteDir, String localDir, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return batchDownload(remoteDir, localDir, f -> pattern.matcher(f.getName()).matches());
    }

    /**
     * 根据关键字批量下载
     * @param remoteDir
     * @param localDir
     * @param keyword 文件名含该关键字
     * @return 下载数
     */
    public static int batchDownloadByKeyword(String remoteDir, String localDir, String keyword) {
        return batchDownload(remoteDir, localDir, f -> f.getName().contains(keyword));
    }
    }


