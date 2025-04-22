package com.st.tools.springbootweb.utils.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <li>在yaml中配置需要脱敏的字段列表</li>
 * <li>配置被封装成list-》在过滤器或拦截器中脱敏处理</li>
 */
@Data
@Component
@ConfigurationProperties(prefix = "log")
public class LogProperties {
    /**
     * 需要脱敏的字段列表，如 password、idCard、phone
     */
    private List<String> sensitiveFields;
}

