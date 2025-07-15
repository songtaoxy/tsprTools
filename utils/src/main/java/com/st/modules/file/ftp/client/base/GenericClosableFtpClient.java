package com.st.modules.file.ftp.client.base;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * <pre>
 * - 支持自动关闭连接资源: 因为实现了AutoCloseable, 所以支持try-with-resources 写法.
 * - - 即:添加了 @Override close()，其默认行为是调用 disconnect()，使得所有实现类自动支持 try-with-resources
 * - 向下兼容,即手动调用 disconnect() 仍可用
 * </pre>
 */
public abstract class GenericClosableFtpClient implements AutoCloseable{

    /**
     * 返回协议类型，如 ftp 或 sftp
     */
    public abstract String getProtocol();



    /**
     * 判断文件是否存在
     *
     * @param filePath 全路径
     * @return 是否存在
     */
    public abstract boolean fileExists(String filePath) throws Exception;

    /**
     * 判断目录是否存在
     *
     * @param remoteDir 远程目录
     * @return 是否存在
     */
    public abstract boolean directoryExists(String remoteDir) throws Exception;

    /**
     * 递归创建远程目录
     * @param remoteDir 远程目录
     */
    public abstract void createDirectory(String remoteDir) throws Exception;


    /**
     * 上传多个文件, 到远程指定目录; 能传一个就能传多个; 一个文件也可以调用该方法
     * @param files
     * @param remoteDir
     * @return
     */
    public abstract int batchUploadFiles(List<File> files, String remoteDir);


    public abstract boolean uploadString(String filename, String content);
    public abstract boolean uploadStream(String filename, InputStream in);


        /**
         * 将本地目录上传至远程目录
         * @param localDir
         * @param remoteDir
         * @return
         */
    public abstract int uploadDirectoryWithStructure( File localDir, String remoteDir);


    /**
     * 文件重命名
     * @param oldPath
     * @param newPath
     * @return
     * @throws IOException
     */
    public abstract boolean renameRemoteFile(String oldPath, String newPath) throws IOException;


        /**
             * @param sourcePath 原始文件的完整路径，如 "/source/abc.txt"
                * @param targetDir 目标目录路径，如 "/archive/2025/07"
                * @throws IOException 移动失败时抛出
             */
    public abstract void moveFileToDirectory( String sourcePath, String targetDir) throws IOException;


    public abstract Map<String, List<String>> batchDownload(String remoteDir, String localDir, Predicate<FTPFile> filter);
        /**
         * Usage: 手动关闭资源; 每个功能, 获取client, 即用即关; 如果再次需要, 再次获取client, 使用完后再关闭;
         * <pre>
         * {@code
         * GenericFtpClient client = null;
         * try {
         *     client = FtpClientProvider.connect("ftpA");
         *     client.upload("/upload", "file.txt", new FileInputStream("file.txt"));
         * } finally {
         *     FtpCloser.closeQuietly(client);
         * }
         *
         * }
         * </pre>
         */
    public abstract void disconnect();


    /**
     * <pre>
     * - 自动关闭资源，适配 try-with-resources
     * - 其默认行为是调用 disconnect()，使得所有实现类自动支持 try-with-resources
     * </pre>
     *
     * <p></p>
     * Usage: 注意, 代码中使用了try
     * <pre>
     *     {@code
     *     try (GenericFtpClient client = FtpClientProvider.connect("ftpA")) {
     *         client.upload("/upload", "file.txt", new FileInputStream("local.txt"));
     *         client.download("/upload", "file.txt", new FileOutputStream("download.txt"));
     *         // 自动调用 client.close() → disconnect()
     *     }catch(){
     *
     *     }finaly{}
     *     }
     * </pre>
     */
    @Override
    public final void close() {
        disconnect();
    }
}

