package com.st.modules.config.deprecated;



import com.st.modules.config.DynamicAppConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 该类可用, 但功能简单. 更智能的, ref {@link DynamicAppConfig}
 */
@Slf4j
public class AppConfigUtils {

    // 配置存储（线程安全，不可变Map）
    private static final Map<String, String> CONFIG_MAP;

    // 加载配置，静态只加载一次，保证线程安全
    static {
        CONFIG_MAP = loadEnvConfig();
        log.info(CONFIG_MAP.toString());
    }

    // 加载配置，静态只加载一次，保证线程安全
    private static Map<String, String> loadEnvConfig() {

        // 先从JVM参数里拿-DappEnv=xxx，如果没有就用默认dev
        String env = System.getProperty("appEnv", "dev").toLowerCase();
        String fileName = "app_" + env + ".properties";
        Properties properties = new Properties();
        try (InputStream in = AppConfigUtils.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) throw new RuntimeException("配置文件不存在: " + fileName);
            properties.load(in);
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件失败: " + fileName, e);
        }
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return Collections.unmodifiableMap(map);
    }

    // 获取配置值（key 不存在返回 null）
    public static String get(String key) {
        return CONFIG_MAP.get(key);
    }

    // 获取配置值，支持默认值
    public static String get(String key, String defaultValue) {
        String value = CONFIG_MAP.get(key);
        return value != null ? value : defaultValue;
    }

    // 获取所有配置Map（只读，不可修改）
    public static Map<String, String> getAll() {
        return CONFIG_MAP;
    }

}

