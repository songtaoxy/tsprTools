package com.st.modules.file.ftp.config.base;

import java.util.Map;

public class FtpClientConfig {
    private String protocol;
    private String host;
    private int port;
    private String username;
    private String password;
    private Map<String, String> paths;

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Map<String, String> getPaths() { return paths; }
    public void setPaths(Map<String, String> paths) { this.paths = paths; }

    public String getProtocol() {
        return protocol != null ? protocol.toLowerCase() : "ftp";
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}

