package com.st.modules.file.ftp.config.springboot;


import com.st.modules.file.ftp.config.base.FtpClientConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * resources/application.yaml配置示例
 * <pre>
 * {@code
 *  ftp:
 *   clients:
 *     ftpA:
 *       protocol: ftp
 *       host: 192.168.1.100
 *       port: 21
 *       username: user
 *       password: pass
 *       paths:
 *         upload: /upload
 *         download: /download
 *     sftpB:
 *       protocol: sftp
 *       host: 192.168.1.101
 *       port: 22
 *       username: user2
 *       password: pass2
 *       paths:
 *         upload: /data/in
 * }
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "ftp")
public class FtpProperties {
    private Map<String, FtpClientConfig> clients;

    public Map<String, FtpClientConfig> getClients() {
        return clients;
    }

    public void setClients(Map<String, FtpClientConfig> clients) {
        this.clients = clients;
    }
}
