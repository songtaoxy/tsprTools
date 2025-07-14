package com.st.modules.file.ftpv2.client.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     * 上传文件到指定目录，覆盖同名文件
     *
     * @param remoteDir 远程目录，如 /upload
     * @param fileName  上传文件名，如 a.txt
     * @param input     输入流
     */
    public abstract void upload(String remoteDir, String fileName, InputStream input) throws Exception;

    /**
     * 从指定目录下载文件
     *
     * @param remoteDir 远程目录，如 /upload
     * @param fileName  文件名，如 a.txt
     * @param output    输出流
     */
    public abstract void download(String remoteDir, String fileName, OutputStream output) throws Exception;

    /**
     * 判断文件是否存在
     *
     * @param remoteDir 目录路径
     * @param fileName  文件名
     * @return 是否存在
     */
    public abstract boolean fileExists(String remoteDir, String fileName) throws Exception;

    /**
     * 判断目录是否存在
     *
     * @param remoteDir 远程目录
     * @return 是否存在
     */
    public abstract boolean directoryExists(String remoteDir) throws Exception;

    /**
     * 创建远程目录（单层或多层）
     *
     * @param remoteDir 远程目录
     */
    public abstract void createDirectory(String remoteDir) throws Exception;


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

