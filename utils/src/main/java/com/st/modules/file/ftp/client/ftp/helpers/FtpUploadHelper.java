package com.st.modules.file.ftp.client.ftp.helpers;

import com.st.modules.file.ftp.client.ftp.deprecared.FTPClientFactory;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTPClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.st.modules.file.ftpv1.FTPClientFactory.getFtpClient;

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


    // 上传字符串内容为文件（如txt、csv等）
    public static boolean uploadString(FTPClient ftpClient, String filename, String content) {
        try {
//            FTPClient ftpClient = FTPClientFactory.getFtpClient();
            InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            boolean done = ftpClient.storeFile(filename, input);
            return done;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 上传输入流
    public static boolean uploadStream(FTPClient ftpClient, String filename, InputStream in) {
        try {
//            FTPClient ftpClient = FTPClientFactory.getFtpClient();
            InputStream input = in ;
            boolean done = ftpClient.storeFile(filename, input);
            return done;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
