package com.st.modules.file.ftp.config.springboot;
import com.st.modules.file.ftp.config.base.FtpConfigRegistry;
import com.st.modules.file.ftp.config.base.FtpClientConfig;
import com.st.modules.file.ftp.config.manul.FtpYamlLoader;

import java.io.IOException;
import java.util.Map;

/**
 * <b><u>统一配置加载入口</u></b>：支持 Spring Boot 自动绑定，也支持手动加载 YAML。
 */
public class FtpConfigBootstrap {

    /**
     * Spring Boot 注入的配置（如果有）
     */
    private static FtpProperties ftpProperties;

    /**
     * 外部显式调用，初始化配置注册中心
     */
    public static void initConfig() {
        if (ftpProperties != null) {
            // 来自 Spring Boot 配置
            FtpConfigRegistry.init(ftpProperties.getClients());
        } else {
            // 非 Spring 环境，手动加载 YAML 配置
            try {
                Map<String, FtpClientConfig> map =
                        FtpYamlLoader.loadFromClasspath("ftp/ftp-config-dev.yml");
                FtpConfigRegistry.init(map);
            } catch (IOException e) {
                throw new RuntimeException("初始化 FTP 配置失败", e);
            }
        }
    }

    /**
     * Spring 注入配置属性时调用，保存到静态变量
     */
    public static void setFtpProperties(FtpProperties props) {
        FtpConfigBootstrap.ftpProperties = props;
    }
    /**
     * 判断当前是否为 Spring 配置模式
     */
    public static boolean isSpringConfigMode() {
        return ftpProperties != null;
    }
}

