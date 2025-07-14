package com.st.modules.file.ftpv2.provider;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.st.modules.file.ftpv2.client.base.GenericClosableFtpClient;
import com.st.modules.file.ftpv2.client.ftp.ApacheFtpClient;
import com.st.modules.file.ftpv2.client.sftp.JschSftpClient;
import com.st.modules.file.ftpv2.config.FtpClientConfig;
import com.st.modules.file.ftpv2.config.FtpConfigRegistry;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
public class FtpClientProvider {
    public static GenericClosableFtpClient connect(String clientKey) throws IOException, JSchException {
        FtpClientConfig cfg = FtpConfigRegistry.getClientConfig(clientKey);
        String protocol = cfg.getProtocol().toLowerCase();

        if ("ftp".equals(protocol)) {
            FTPClient ftp = new FTPClient();
            ftp.connect(cfg.getHost(), cfg.getPort());
            ftp.login(cfg.getUsername(), cfg.getPassword());
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            return new ApacheFtpClient(ftp);
        } else if ("sftp".equals(protocol)) {
            JSch jsch = new JSch();
            Session session = jsch.getSession(cfg.getUsername(), cfg.getHost(), cfg.getPort());
            session.setPassword(cfg.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            return new JschSftpClient(sftp, session);
        } else {
            throw new IllegalArgumentException("不支持的协议: " + protocol);
        }
    }
}

