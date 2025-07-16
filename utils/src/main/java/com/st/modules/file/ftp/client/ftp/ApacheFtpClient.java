package com.st.modules.file.ftp.client.ftp;

import com.st.modules.file.ftp.client.base.GenericClosableFtpClient;
import com.st.modules.file.ftp.client.ftp.deprecared.FtpHelper;
import com.st.modules.file.ftp.client.ftp.helpers.*;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Usage: 如何关闭资源? 手动关闭? 或try wit resource关闭. ref{@link com.st.modules.file.ftp.main.Main}
 * <pre>
 * {@code
 *    try (
 *                 GenericClosableFtpClient client = FtpClientProvider.connect(clientKey);
 *                 InputStream input = ClassPathResourcesUtils.getClasspathFile("ftp/test/local.txt");
 *                 OutputStream output = new FileOutputStream("copy-try.txt")
 *         ) {
 *             client.upload(path, "file-try.txt", input);
 *             client.download(path, "file-try.txt", output);
 *         }
 * }
 * </pre>
 */
public class ApacheFtpClient extends GenericClosableFtpClient {
    private final FTPClient ftpClient;

    public ApacheFtpClient(FTPClient ftp) {
        this.ftpClient = ftp;
    }

    @Override
    public String getProtocol() {
        return "ftp";
    }


    @Override
    public boolean fileExists(String filePath) throws IOException {
        return FtpExistHelper.fileExists(ftpClient, filePath);
    }


    @Override
    public boolean directoryExists(String remoteDir) throws IOException {
        return FtpExistHelper.directoryExists(ftpClient, remoteDir);
    }

    @SneakyThrows
    @Override
    public void makeDirRecursively(String remoteDir) throws IOException {
//        FtpHelper.createDirectoryIfNotExists(ftpClient, remoteDir);
        FtpMakeDirHelper.makeRemoteDirs(ftpClient, remoteDir);
    }


    @Override
    public int batchUploadFiles(List<File> files, String remoteDir) {
        return FtpUploadHelper.batchUploadFiles(ftpClient, files, remoteDir);
    }

    // ===========================
    // upload
    // ===========================
    @Override
    public boolean uploadStream( String dirPath, String filename, InputStream in) {
         return FtpUploadHelper.uploadStream(ftpClient, dirPath, filename, in);

    }
    @Override
    public boolean uploadText(String path, String filename, String content) {
        return FtpUploadHelper.uploadText(ftpClient,path,filename,content);
    }

    @Override
    public boolean uploadBytes(String path, String filename, byte[] data) {
        return FtpUploadHelper.uploadBytes(ftpClient,path,filename,data);
    }

    @Override
    public boolean uploadFile(String path, String filename, File file) {
        return FtpUploadHelper.uploadFile(ftpClient,path,filename,file);
    }

    @Override
    public boolean uploadPath(String path, String filename, String localFilePath) {
        return FtpUploadHelper.uploadPath(ftpClient,path,filename,localFilePath);
    }

    @Override
    public int uploadDirectoryWithStructure(File localDir, String remoteDir) {
        return FtpUploadHelper.uploadDirectoryWithStructure(ftpClient, localDir, remoteDir);
    }


    // ===========================
    // rename
    // ===========================
    @Override
    public boolean renameRemoteFile(String oldPath, String newPath) throws IOException {
        return FtpRenameHelper.renameRemoteFile(ftpClient, oldPath, newPath);
    }


    @Override
    public void moveFileToDirectory(String sourcePath, String targetDir) throws IOException {
        FtpMoveHelper.moveFileToDirectory(ftpClient, sourcePath,targetDir);
    }

    @Override
    public Map<String, List<String>> batchDownload(String remoteDir, String localDir, Predicate<FTPFile> filter) {
        return FtpDownLoadHelper.batchDownload(ftpClient, remoteDir, localDir, filter);
    }


    @Override
    public void disconnect() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
            } catch (IOException ignored) {}
            try {
                ftpClient.disconnect();
            } catch (IOException ignored) {}
        }
    }
}

