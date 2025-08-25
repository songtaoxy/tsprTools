package com.st.modules.file.ftp2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.security.PrivateKey;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FtpServer {
    private String code;
    private String name;
    private String desc;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean passiveMode;
    private String path;

    public static String buildSummaryInfo(FtpServer ftpServer) {
        return ftpServer.getUser() + "@" + ftpServer.getHost() + ":" + ftpServer.getPort()+ File.separator + ftpServer.getPath();
    }
}
