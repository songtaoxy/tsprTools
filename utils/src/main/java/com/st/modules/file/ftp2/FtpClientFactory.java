package com.st.modules.file.ftp2;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.net.SocketException;

/**
 * 概述
 * 每次请求时按 code 建立 FTPClient 并返回 调用方用后必须关闭
 * 功能清单
 * 1 create(String code) 基于注册表实体新建连接 登录 设置二进制与被动模式
 * 2 safeClose 安全断开连接
 * 3 可选超时设置 connectTimeout dataTimeout controlKeepAliveTimeout
 * 使用示例
 * FTPClient c = FtpClientFactory.create("FGLS"); try { ... } finally { FtpClientFactory.safeClose(c); }
 * 注意事项
 * FTPClient 非线程安全 不要跨线程共享
 * 入参与出参与异常说明
 * 登录失败或网络异常抛 RuntimeException 未找到 code 抛 NoSuchElementException
 */
final class FtpClientFactory {
    private FtpClientFactory() {}
    public static FTPClient create(String code) {
        FtpServer cfg = FtpConfigRegistry.getServerByCode(code);
        return create(cfg);
    }
    public static FTPClient create(FtpServer cfg) {
        FTPClient c = new FTPClient();
        // 可按需设置超时 c.setConnectTimeout(10000); c.setDataTimeout(20000);
        try {
            c.connect(cfg.getHost(), cfg.getPort());
            boolean ok = c.login(cfg.getUser(), cfg.getPassword());
            if (!ok) { safeClose(c); throw new RuntimeException("FTP login failed code=" + cfg.getCode()); }
            if (cfg.isPassiveMode()) c.enterLocalPassiveMode();
            c.setFileType(FTP.BINARY_FILE_TYPE);
            c.setBufferSize(64 * 1024);
            c.setAutodetectUTF8(true);
            return c;
        } catch (SocketException e) {
            safeClose(c);
            throw new RuntimeException("FTP socket error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        } catch (Exception e) {
            safeClose(c);
            throw new RuntimeException("FTP connect error code=" + cfg.getCode() + " msg=" + e.getMessage(), e);
        }
    }
    public static void safeClose(FTPClient c) {
        if (c == null) return;
        try { if (c.isConnected()) { c.logout(); c.disconnect(); } } catch (Exception ignore) {}
    }
}
