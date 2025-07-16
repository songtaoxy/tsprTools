package com.st.modules.file.ftp.client.ftp.helpers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.st.modules.file.deprecated.ftpv1.FTPClientFactory.getFtpClient;

@Slf4j
public class FtpUploadHelper {


    /**
     * <pre>
     * 批量上传多个本地文件到FTP同一目录; 即使是一个, 也封装成list调该方法, 而不要单独调用uploadFileToRemoteDir
     * </pre>
     *
     * Usage
     * <pre>
     * {@code
     *      // 上传多个指定文件
     * List<File> files = Arrays.asList(
     *     new File("/local/a.txt"),
     *     new File("/local/b.tar.gz"),
     *     new File("/local/c.txt")
     * );
     * int n2 = FtpUtils.batchUploadFiles(files, "/remote/ftpDir");
     * }
     * </pre>
     * @param files     文件列表
     * @param remoteDir 远程目录
     * @return 上传成功的文件数
     */
    public static int batchUploadFiles(FTPClient ftpClient,List<File> files, String remoteDir) {
        int success = 0;
        for (File file : files) {
            if (file != null && file.isFile()) {
                boolean ok = uploadFileToRemoteDir(ftpClient, file, remoteDir);
                if (ok) success++;
            }
        }
        return success;
    }

    /**
     * 上传本地文件到远程指定目录
     * @param localFile 本地文件
     * @param remoteDir 远程目录
     * @return
     */
    public static boolean uploadFileToRemoteDir(FTPClient ftpClient, File localFile, String remoteDir) {
//        FTPClient ftpClient = null;
        try {
//            ftpClient = getFtpClient();
            ftpClient.changeWorkingDirectory(remoteDir);

            // 不存在则尝试创建远程目录
            String[] dirs = remoteDir.split("/");
            String path = "";
            for (String d : dirs) {
                if (d.isEmpty()) continue;
                path += "/" + d;
                ftpClient.makeDirectory(path);
            }
            ftpClient.changeWorkingDirectory(remoteDir);

            try (InputStream input = new FileInputStream(localFile)) {
                return ftpClient.storeFile(localFile.getName(), input);
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



    /**
     * 上传输入流内容为 FTP 文件（自动创建目录 + 关闭流 + 异常上抛）
     *
     * <p></p>
     * 设计
     * <pre>
     * - 如果 path 不存在，自动递归创建远程目录
     * - 异常时记录日志，并将异常重新抛出
     * - 使用 try-with-resources 自动关闭流
     * </pre>
     *
     * <p></p>
     * Usage
     * <pre>
     *  {@code
     *  public class FtpUploadExample {
     *     public static void main(String[] args) {
     *         String clientKey = "ftpA"; // 对应配置文件中的 key
     *         String path = "/upload/subdir"; // 目标路径（不存在将自动创建）
     *         String filename = "hello.txt";  // 上传文件名
     *         String content = "Hello FTP! 上传示例内容";
     *
     *         try (GenericClosableFtpClient client = FtpClientProvider.connect(clientKey);
     *              InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
     *
     *             boolean success = FtpHelper.uploadStream(client, path, filename, input);
     *             if (success) {
     *                 System.out.println("上传成功");
     *             }
     *
     *         } catch (Exception e) {
     *             System.err.println("上传失败: " + e.getMessage());
     *             e.printStackTrace();
     *         }
     *     }
     * }
     *  }
     * </pre>
     *
     * @param client   已建立连接的 FTP 客户端
     * @param path     FTP 远程目录路径，如 /upload/sub
     * @param filename 文件名，如 test.txt
     * @param input    输入流（上传源），方法内部自动关闭
     * @return 上传成功返回 true，否则抛出异常
     * @throws RuntimeException 上传或目录创建失败时抛出异常
     */
    public static boolean uploadStream(FTPClient client, String path, String filename, InputStream input) {
        try (InputStream in = input) {
            // 检查并递归创建目录
           FtpMakeDirHelper.makeRemoteDirs(client,path);

            // 执行上传
            boolean success = client.storeFile(path + "/" + filename, in);

            if (!success) {
                throw new RuntimeException("FTP上传失败: " + path + "/" + filename);
            }
            return true;
        } catch (Exception e) {
            log.error("FTP上传失败, path={}, filename={}", path, filename, e);
            throw new RuntimeException("FTP上传异常: " + path + "/" + filename, e);
        }
    }

    /**
     * 上传字符串内容为 FTP 文件
     *
     * @param client   已建立连接的 FTPClient 实例
     * @param path     远程目录路径，如 /upload
     * @param filename 上传后保存的文件名，如 test.txt
     * @param content  要上传的文本内容
     * @return 上传是否成功
     */
    public static boolean uploadText(FTPClient client, String path, String filename, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return uploadBytes(client, path, filename, bytes);
    }

    /**
     * 上传字节数组为 FTP 文件
     *
     * @param client   已建立连接的 FTPClient 实例
     * @param path     远程目录路径，如 /upload
     * @param filename 上传后保存的文件名，如 test.txt
     * @param data     要上传的字节数据
     * @return 上传是否成功
     */
    public static boolean uploadBytes(FTPClient client, String path, String filename, byte[] data) {
        try (InputStream in = new ByteArrayInputStream(data)) {
            return uploadStream(client, path, filename, in);
        } catch (IOException e) {
            throw new RuntimeException("上传字节数组失败: " + path + "/" + filename, e);
        }
    }

    /**
     * 上传本地文件对象为 FTP 文件
     *
     * @param client   已建立连接的 FTPClient 实例
     * @param path     远程目录路径，如 /upload
     * @param filename 上传后保存的文件名，如 test.txt
     * @param file     本地文件对象
     * @return 上传是否成功
     */
    public static boolean uploadFile(FTPClient client, String path, String filename, File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("本地文件不存在: " + file);
        }
        try (InputStream in = new FileInputStream(file)) {
            return uploadStream(client, path, filename, in);
        } catch (IOException e) {
            throw new RuntimeException("上传本地文件失败: " + path + "/" + filename, e);
        }
    }

    /**
     * 上传本地路径文件为 FTP 文件
     *
     * @param client        已建立连接的 FTPClient 实例
     * @param path          远程目录路径，如 /upload
     * @param filename      上传后保存的文件名，如 test.txt
     * @param localFilePath 本地文件路径
     * @return 上传是否成功
     */
    public static boolean uploadPath(FTPClient client, String path, String filename, String localFilePath) {
        File file = new File(localFilePath);
        return uploadFile(client, path, filename, file);
    }



    /**
     * <pre>
     *  递归上传本地目录到FTP，保持本地目录层级结构
     * - 该方法可直接用于生产场景，适合批量归档、备份、批量自动化等 FTP 批量上传任务
     * - 递归：每遇到目录时自动递归，每遇到文件则保持原有层级上传。
     * - 分层创建：远程目录层级不存在会自动分层创建（makeRemoteDirs）。
     * - 相对路径：上传时用localBase做基准，拼接成远端相对路径。
     * - 平台兼容：文件分隔符统一为 /，兼容 Linux/Windows。
     * - 健壮性：各种边界判断，上传失败自动捕获异常。
     * </pre>
     *
     * Usage
     * <pre>
     * {@code
     * File localDir = new File("/Users/xxx/testdir"); // 本地多层目录
     * String remoteRoot = "/data/ftp_upload";         // FTP目标根目录
     * int count = FtpUploadPathchUtils.uploadDirectoryWithStructure(localDir, remoteRoot);
     * System.out.println("成功上传文件数: " + count);
     * }
     * </pre>
     * @param localDir 本地根目录
     * @param remoteDir 远程FTP根目录
     * @return 上传成功文件数
     */
    public static int uploadDirectoryWithStructure(FTPClient ftpClient, File localDir, String remoteDir) {
        return uploadDirectoryWithStructure(ftpClient,localDir, remoteDir, localDir.getAbsolutePath());
    }

    // 内部递归方法
    @SneakyThrows
    private static int uploadDirectoryWithStructure(FTPClient ftpClient, File local, String remoteBase, String localBase) {
        int success = 0;
        if (!local.exists()) return 0;
        if (local.isFile()) return 0;
        File[] files = local.listFiles();
        if (files == null) return 0;

        for (File file : files) {
            String relative = file.getAbsolutePath().substring(localBase.length()).replace(File.separatorChar, '/');
            String remotePath = remoteBase + relative;
            if (file.isDirectory()) {
                // 创建远程目录（分层创建）
                makeRemoteDirs(remoteBase, relative);
                // 递归
                success += uploadDirectoryWithStructure(ftpClient, file, remoteBase, localBase);
            } else {
                boolean ok = uploadSingleFileWithRemotePath(ftpClient,file, remoteBase + relative);
                if (ok) success++;
            }
        }
        return success;
    }

    /**
     * 上传单个文件到指定FTP路径（会自动分隔目录与文件名）
     */
    public static boolean uploadSingleFileWithRemotePath(FTPClient ftpClient, File localFile, String remoteFullPath) {
//        FTPClient ftpClient = null;
        try {
//            ftpClient = getFtpClient();
            String remoteDir = remoteFullPath.substring(0, remoteFullPath.lastIndexOf('/'));
            String remoteName = remoteFullPath.substring(remoteFullPath.lastIndexOf('/') + 1);
            // 创建目录
            makeRemoteDirs(remoteDir, "");
            ftpClient.changeWorkingDirectory(remoteDir);

            try (InputStream input = new FileInputStream(localFile)) {
                return ftpClient.storeFile(remoteName, input);
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

    /**
     * 递归创建FTP远程目录
     */
    public static void makeRemoteDirs(String remoteBase, String relativePath) throws Exception {
        FTPClient ftpClient = getFtpClient();
        String path = remoteBase;
        String[] dirs = relativePath.split("/");
        for (String dir : dirs) {
            if (dir.isEmpty()) continue;
            path += "/" + dir;
            ftpClient.makeDirectory(path);
        }
        ftpClient.disconnect();
    }

}
