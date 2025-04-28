package com.st.tools.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: st
 * @date: 2021/6/22 03:38
 * @version: 1.0
 * @description:
 */
@Configuration
public class XxxConfiguration {

@Bean
	public UserConfig getUserConfig(){
	UserConfig userConfig = new UserConfig();

	userConfig.setName("hi============================");
    return userConfig;
}

}
