package com.st.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 通常 Spring Boot 会自动开启，这里显式指定
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
}
