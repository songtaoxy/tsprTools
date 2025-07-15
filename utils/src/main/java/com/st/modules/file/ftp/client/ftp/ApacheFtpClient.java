package com.st.modules.file.ftp.client.ftp;

import com.st.modules.file.ftp.client.base.GenericClosableFtpClient;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public void upload(String remoteDir, String fileName, InputStream input) throws IOException {
        FtpHelper.ensureRecursiveDirectory(ftp, remoteDir);
        ftp.changeWorkingDirectory(remoteDir);
        ftp.storeFile(fileName, input);
    }

    @Override
    public void download(String remoteDir, String fileName, OutputStream output) throws IOException {
        ftp.changeWorkingDirectory(remoteDir);
        ftp.retrieveFile(fileName, output);
    }

    @Override
    public boolean fileExists(String remoteDir, String fileName) throws IOException {
        return FtpHelper.fileExists(ftp, remoteDir, fileName);
    }

    @Override
    public boolean directoryExists(String remoteDir) throws IOException {
        return FtpHelper.directoryExists(ftp, remoteDir);
    }

    @Override
    public void createDirectory(String remoteDir) throws IOException {
        FtpHelper.createDirectoryIfNotExists(ftp, remoteDir);
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

