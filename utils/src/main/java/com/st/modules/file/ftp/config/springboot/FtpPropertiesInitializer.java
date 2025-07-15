package com.st.modules.file.ftp.config.springboot;


import org.springframework.stereotype.Component;

/**
 * <b><u>Spring Boot 注入桥接器</u></b>：负责把 @ConfigurationProperties 注入到静态入口中。
 */
@Component
public class FtpPropertiesInitializer {

    public FtpPropertiesInitializer(FtpProperties properties) {
        FtpConfigBootstrap.setFtpProperties(properties);
        FtpConfigBootstrap.initConfig();
    }
}
