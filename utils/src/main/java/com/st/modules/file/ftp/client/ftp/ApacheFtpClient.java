package com.st.modules.file.ftp.client.ftp;

import com.st.modules.file.ftp.client.base.GenericClosableFtpClient;
import com.st.modules.file.ftp.client.ftp.deprecared.FtpHelper;
import com.st.modules.file.ftp.client.ftp.helpers.*;
import com.st.modules.file.ftp.config.base.FtpConfigRegistry;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
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
    private final FTPClient ftp;

    public ApacheFtpClient(FTPClient ftp) {
        this.ftp = ftp;
    }

    @Override
    public String getProtocol() {
        return "ftp";
    }


    @Override
    public boolean fileExists(String filePath) throws IOException {
        return FtpExistHelper.fileExists(ftp, filePath);
    }


    @Override
    public boolean directoryExists(String remoteDir) throws IOException {
        return FtpExistHelper.directoryExists(ftp, remoteDir);
    }

    @SneakyThrows
    @Override
    public void createDirectory(String remoteDir) throws IOException {
        FtpHelper.createDirectoryIfNotExists(ftp, remoteDir);
        FtpMakeDirHelper.makeRemoteDirs(ftp, remoteDir);
    }


    @Override
    public int batchUploadFiles(List<File> files, String remoteDir) {
        return FtpUploadHelper.batchUploadFiles(ftp, files, remoteDir);
    }

    @Override
    public boolean uploadString( String filename, String content) {
        return FtpUploadHelper.uploadString(ftp, filename, content);
    }

    @Override
    public boolean uploadStream( String filename, InputStream in) {
         return FtpUploadHelper.uploadStream(ftp, filename, in);

    }

    @Override
    public int uploadDirectoryWithStructure(File localDir, String remoteDir) {
        return FtpUploadHelper.uploadDirectoryWithStructure(ftp, localDir, remoteDir);
    }

    @Override
    public boolean renameRemoteFile(String oldPath, String newPath) throws IOException {
        return FtpRenameHelper.renameRemoteFile(ftp, oldPath, newPath);
    }


    @Override
    public void moveFileToDirectory(String sourcePath, String targetDir) throws IOException {
        FtpMoveHelper.moveFileToDirectory(ftp, sourcePath,targetDir);
    }

    @Override
    public Map<String, List<String>> batchDownload(String remoteDir, String localDir, Predicate<FTPFile> filter) {
        return FtpDownLoadHelper.batchDownload(ftp, remoteDir, localDir, filter);
    }


    @Override
    public void disconnect() {
        if (ftp.isConnected()) {
            try {
                ftp.logout();
            } catch (IOException ignored) {}
            try {
                ftp.disconnect();
            } catch (IOException ignored) {}
        }
    }
}

