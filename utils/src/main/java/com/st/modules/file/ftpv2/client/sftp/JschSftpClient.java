package com.st.modules.file.ftpv2.client.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.st.modules.file.ftpv2.client.base.GenericClosableFtpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public void upload(String remoteDir, String fileName, InputStream input) throws Exception {
        SftpHelper.ensureRecursiveDirectory(sftp, remoteDir);
        sftp.cd(remoteDir);
        sftp.put(input, fileName);
    }

    @Override
    public void download(String remoteDir, String fileName, OutputStream output) throws Exception {
        sftp.cd(remoteDir);
        sftp.get(fileName, output);
    }

    @Override
    public boolean fileExists(String remoteDir, String fileName) {
        return SftpHelper.fileExists(sftp, remoteDir + "/" + fileName);
    }

    @Override
    public boolean directoryExists(String remoteDir) {
        return SftpHelper.directoryExists(sftp, remoteDir);
    }

    @Override
    public void createDirectory(String remoteDir) throws Exception {
        SftpHelper.createDirectoryIfNotExists(sftp, remoteDir);
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
