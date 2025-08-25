package com.st.modules.file.ftp2;

/**
 * 概述
 * 以 -DappEnv=dev|test|prod 选择类路径下 config_{env}.yml
 * 启动时加载配置为实体索引；每次请求按 code 临时建连并返回 FTPClient
 * 功能清单
 * 1 EnvResolver 解析环境与资源名
 * 2 YamlLoader 加载 YAML 与字段校验
 * 3 FtpServer 配置实体
 * 4 FtpConfigRegistry 启动期加载并索引实体
 * 5 FtpClientFactory 按 code 创建 FTPClient
 * 使用示例
 * FtpServer s = FtpConfigRegistry.getServerByCode("FGLS");
 * FTPClient c = FtpClientFactory.create("FGLS"); try { ... } finally { FtpClientFactory.safeClose(c); }
 * 注意事项
 * 1 FTPClient 非线程安全 每次请求独立实例 用完务必关闭
 * 2 请在类路径提供 config_{env}.yml
 * 入参与出参与异常说明
 * 解析失败与连接失败抛 RuntimeException 未找到 code 抛 NoSuchElementException
 */
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.InputStream;
import java.net.SocketException;
import java.util.*;

/** 环境解析工具 */
final class EnvResolver {
    private EnvResolver() {
    }

    /**
     * 概述
     * 解析运行环境 优先系统属性 appEnv 其次环境变量 APP_ENV 默认 dev
     * 功能清单
     * 1 读取系统属性与环境变量
     * 2 返回标准化 env 字符串
     * 使用示例
     * String env = EnvResolver.resolveEnv();
     * 注意事项
     * 仅用于选择类路径资源
     * 入参与出参与异常说明
     * 无入参 无异常
     */
    public static String resolveEnv() {
        String p = System.getProperty("appEnv");
        if (p != null && !p.trim().isEmpty()) return p.trim();
        String e = System.getenv("APP_ENV");
        if (e != null && !e.trim().isEmpty()) return e.trim();
        return "dev";
    }

    /**
     * 概述
     * 生成类路径资源名并校验存在
     * 功能清单
     * 1 组装 config_{env}.yml
     * 2 检查资源是否存在
     * 使用示例
     * String res = EnvResolver.resolveClasspathResource("dev");
     * 注意事项
     * 仅校验存在性 不解析
     * 入参与出参与异常说明
     * 资源不存在抛 IllegalArgumentException
     */
    public static String resolveClasspathResource(String env) {
        String name = "config_" + env + ".yml";
        String dir = "ftp" + File.separator;
        String path = dir + name;
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (in == null) throw new IllegalArgumentException("Classpath resource not found " + path);
        try {
            in.close();
        } catch (Exception ignore) {
        }
        return path;
    }

}