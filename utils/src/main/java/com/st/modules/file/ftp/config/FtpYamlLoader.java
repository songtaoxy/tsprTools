package com.st.modules.file.ftp.config;

import com.st.modules.file.ftp.constant.constant.FtpConfigKeys;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     - 依赖: SnakeYAML. 见maven/pom中
 * </pre>
 */
public class FtpYamlLoader {
    public static Map<String, FtpClientConfig> loadFromClasspath(String fileName) throws IOException {
        InputStream input = FtpYamlLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (input == null) {
            throw new FileNotFoundException("类路径下未找到文件: " + fileName);
        }

        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(input);
        Map<String, Object> ftp = (Map<String, Object>) root.get(FtpConfigKeys.FTP_ROOT);
        Map<String, Object> clients = (Map<String, Object>) ftp.get(FtpConfigKeys.CLIENTS);

        Map<String, FtpClientConfig> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : clients.entrySet()) {
            String clientKey = entry.getKey();
            Map<String, Object> raw = (Map<String, Object>) entry.getValue();

            FtpClientConfig cfg = new FtpClientConfig();
            cfg.setProtocol((String) raw.get(FtpConfigKeys.PROTOCOL));
            cfg.setHost((String) raw.get(FtpConfigKeys.HOST));
            cfg.setPort((Integer) raw.get(FtpConfigKeys.PORT));
            cfg.setUsername((String) raw.get(FtpConfigKeys.USERNAME));
            cfg.setPassword((String) raw.get(FtpConfigKeys.PASSWORD));

            Map<String, String> paths = new HashMap<>();
            Object pathObj = raw.get(FtpConfigKeys.PATHS);
            if (pathObj instanceof Map) {
                Map<String, Object> rawPaths = (Map<String, Object>) pathObj;
                for (Map.Entry<String, Object> pe : rawPaths.entrySet()) {
                    paths.put(pe.getKey(), String.valueOf(pe.getValue()));
                }
            }
            cfg.setPaths(paths);
            result.put(clientKey, cfg);
        }

        return result;
    }
}

