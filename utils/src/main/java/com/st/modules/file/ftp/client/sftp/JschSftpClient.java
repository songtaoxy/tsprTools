package com.st.modules.file.ftp.client.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.st.modules.file.ftp.client.base.GenericClosableFtpClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class JschSftpClient extends GenericClosableFtpClient {
    private final ChannelSftp sftp;
    private final Session session;

    public JschSftpClient(ChannelSftp sftp, Session session) {
        this.sftp = sftp;
        this.session = session;
    }

    @Override
    public String getProtocol() {
        return "sftp";
    }


    @Override
    public boolean fileExists(String filePath) {
        return SftpHelper.fileExists(sftp, filePath);
    }

    @Override
    public boolean directoryExists(String remoteDir) {
        return SftpHelper.directoryExists(sftp, remoteDir);
    }

    @Override
    public void makeDirRecursively(String remoteDir) throws Exception {
       SftpHelper.createDirectoryIfNotExists(sftp, remoteDir);
    }

    @Override
    public int batchUploadFiles(List<File> files, String remoteDir) {
        return 0;
    }


    @Override
    public boolean uploadStream(String dirPath, String filename, InputStream in) {
        return false;
    }

    @Override
    public boolean uploadText(String path, String filename, String content) {
        return false;
    }

    @Override
    public boolean uploadBytes(String path, String filename, byte[] data) {
        return false;
    }

    @Override
    public boolean uploadFile(String path, String filename, File file) {
        return false;
    }

    @Override
    public boolean uploadPath(String path, String filename, String localFilePath) {
        return false;
    }

    @Override
    public int uploadDirectoryWithStructure(File localDir, String remoteDir) {
        return 0;
    }

    @Override
    public boolean renameRemoteFile(String oldPath, String newPath) throws IOException {
        return false;
    }

    @Override
    public void moveFileToDirectory(String sourcePath, String targetDir) throws IOException {

    }

    @Override
    public Map<String, List<String>> batchDownload(String remoteDir, String localDir, Predicate<FTPFile> filter) {
        return Collections.emptyMap();
    }

    @Override
    public void disconnect() {
        if (sftp.isConnected()) {
            try {
                sftp.exit();
            } catch (Exception ignored) {}
        }
        if (session.isConnected()) {
            session.disconnect();
        }
    }
}
