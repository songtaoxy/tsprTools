package com.st.modules.file.ftp2;

import org.apache.commons.net.ftp.FTPClient;

class DemoMain {
    /**
     * 概述
     * 展示启动加载与按 code 建连流程
     * 功能清单
     * 1 打印当前 env 与资源
     * 2 列出全部实体
     * 3 建立一次连接并 NOOP
     * 使用示例
     * java -DappEnv=dev -cp ... DemoMain
     * 注意事项
     * 确保目标 FTP 可达
     * 入参与出参与异常说明
     * 连接失败抛出 RuntimeException
     */
    public static void main(String[] args) throws Exception {
        System.out.println("env=" + FtpConfigRegistry.env() + " resource=" + FtpConfigRegistry.resource());
        for (FtpServer s : FtpConfigRegistry.getAllServers()) {
            System.out.println("CFG " + s);
        }
//         示例: 按需建立连接
        FTPClient c = FtpClientFactory.create("FGLS");
        try {
            System.out.println("NOOP=" + c.sendNoOp());
        } finally {
            FtpClientFactory.safeClose(c);
        }
    }
}
