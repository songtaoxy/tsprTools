package com.st.modules.file.ftp.provider;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.st.modules.file.ftp.client.base.GenericClosableFtpClient;
import com.st.modules.file.ftp.client.ftp.ApacheFtpClient;
import com.st.modules.file.ftp.client.sftp.JschSftpClient;
import com.st.modules.file.ftp.config.base.FtpClientConfig;
import com.st.modules.file.ftp.config.base.FtpConfigRegistry;
import com.st.modules.file.ftp.constant.enums.FtpProtocolType;
import com.st.modules.json.jackson.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
@Slf4j
public class FtpClientProvider {
    public static GenericClosableFtpClient connect(String clientKey) throws IOException, JSchException {
        FtpClientConfig cfg = FtpConfigRegistry.getClientConfig(clientKey);
        String protocol = cfg.getProtocol().toLowerCase();

        log.info("\n 协议类型:[" + protocol + "], 详情: \n"+ JacksonUtils.toPrettyJson(cfg
        ));

        if (FtpProtocolType.FTP.getValue().equals(protocol)) {
            FTPClient ftp = new FTPClient();
            ftp.connect(cfg.getHost(), cfg.getPort());
            ftp.login(cfg.getUsername(), cfg.getPassword());
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            return new ApacheFtpClient(ftp);
        } else if (FtpProtocolType.SFTP.getValue().equals(protocol)) {
            JSch jsch = new JSch();
            Session session = jsch.getSession(cfg.getUsername(), cfg.getHost(), cfg.getPort());
            session.setPassword(cfg.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            ChannelSftp sftp = (ChannelSftp) session.openChannel(FtpProtocolType.SFTP.getValue());
            sftp.connect();
            return new JschSftpClient(sftp, session);
        } else {
            throw new IllegalArgumentException("不支持的协议: " + protocol);
        }
    }
}

