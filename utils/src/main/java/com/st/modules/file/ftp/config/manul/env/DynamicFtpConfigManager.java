package com.st.modules.file.ftp.config.manul.env;

import com.st.modules.file.classpath.ClassPathResourcesUtils;
import com.st.modules.file.ftp.config.base.FtpClientConfig;
import com.st.modules.file.ftp.config.manul.FtpYamlLoader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class DynamicFtpConfigManager {
    private static final String FILE_PREFIX = "ftp_";
    private static final String FILE_SUFFIX = ".yaml";
    private static final AtomicReference<Map<String, FtpClientConfig>> CONFIGS = new AtomicReference<>();
    private static Path configFile;

    static {
        String env = EnvDetector.getActiveEnv();
        load(env);
        watch(env);
    }

    public static FtpClientConfig get(String clientKey) {
        return CONFIGS.get().get(clientKey);
    }

    public static Map<String, FtpClientConfig> getAll() {
        return CONFIGS.get();
    }

    public static void switchEnv(String newEnv) {
        load(newEnv);
        watch(newEnv);
    }

    private static void load(String env) {
        String fileName = FILE_PREFIX + env + FILE_SUFFIX;
        try (InputStream in = ClassPathResourcesUtils.getClasspathFile(fileName)) {
            Map<String, FtpClientConfig> config = FtpYamlLoader.loadFromStream(in);
            CONFIGS.set(Collections.unmodifiableMap(config));
            configFile = Paths.get(Objects.requireNonNull(DynamicFtpConfigManager.class.getClassLoader().getResource(fileName)).toURI());
        } catch (Exception e) {
            throw new IllegalStateException("加载配置失败", e);
        }
    }

    private static InputStream getClasspathStream(String fileName) throws FileNotFoundException {
        InputStream in = DynamicFtpConfigManager.class.getClassLoader().getResourceAsStream(fileName);
        if (in == null) throw new FileNotFoundException("找不到配置文件: " + fileName);
        return in;
    }

    private static void watch(String env) {
        if (configFile == null) return;
        YamlFileWatcher.watch(configFile, () -> load(env));
    }
}

