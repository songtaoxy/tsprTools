package com.st.modules.file.ftp.config.manul;

import com.st.modules.file.ftp.config.base.FtpClientConfig;
import com.st.modules.file.ftp.constant.constant.FtpConfigKeys;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 手动加载配置: classpath: ftp/ftp-config-dev.yml
 * <pre>
 * - 依赖: SnakeYAML. 见maven/pom中
 * </pre>
 *
 * <p></p>
 * classpath: ftp/ftp-config-dev.yml
 * <pre>
 * {@code
 * ftp:
 *   clients:
 *     ftpA:
 *       protocol: ftp
 *       host: 192.168.1.100
 *       port: 21
 *       username: user
 *       password: pass
 *       paths:
 *         upload: /upload
 *         download: /download
 *     sftpB:
 *       protocol: sftp
 *       host: 192.168.1.101
 *       port: 22
 *       username: user2
 *       password: pass2
 *       paths:
 *         upload: /data/in
 * }
 * </pre>
 */
public class FtpYamlLoader {

    /**
     * <pre>
     *  - 从输入流中加载 YAML 格式的 FTP 配置。
     *  - 该方法专用于从输入流（如 classpath 或外部文件）中读取 YAML 配置，避免写死文件名
     *  -- 流从哪里获取? 比如, 将从classpath获取资源, 转成流, 然后作为参数传入;
     *  -- 写死文件名进行解耦: 获取流, 传入流
     * </pre>
     *
     *
     *
     * <pre>
     * - 手动加载配置: classpath: ftp/ftp-config-dev.yml
     * - 依赖: SnakeYAML. 见maven/pom中
     * </pre>
     *
     * <p></p>
     * classpath: ftp/ftp-config-dev.yml
     * <pre>
     * {@code
     * ftp:
     *   clients:
     *     ftpA:
     *       protocol: ftp
     *       host: 192.168.1.100
     *       port: 21
     *       username: user
     *       password: pass
     *       paths:
     *         upload: /upload
     *         download: /download
     *     sftpB:
     *       protocol: sftp
     *       host: 192.168.1.101
     *       port: 22
     *       username: user2
     *       password: pass2
     *       paths:
     *         upload: /data/in
     * }
     * </pre>
     *
     *
     * @param in 输入流（调用方需自行关闭）
     * @return 解析后的 Map，key 为 clientKey，value 为 FtpClientConfig 实例
     * @throws IllegalArgumentException 若格式不符合预期或内容为空
     */
    public static Map<String, FtpClientConfig> loadFromStream(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("YAML 输入流为 null");
        }

        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(in);
        if (root == null || !root.containsKey(FtpConfigKeys.FTP_ROOT)) {
            throw new IllegalArgumentException("YAML 根节点缺失或格式错误");
        }

        Map<String, Object> ftp = castMap(root.get(FtpConfigKeys.FTP_ROOT));
        Map<String, Object> clients = castMap(ftp.get(FtpConfigKeys.CLIENTS));

        Map<String, FtpClientConfig> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : clients.entrySet()) {
            String clientKey = entry.getKey();
            Map<String, Object> raw = castMap(entry.getValue());

            FtpClientConfig cfg = new FtpClientConfig();
            cfg.setProtocol((String) raw.get(FtpConfigKeys.PROTOCOL));
            cfg.setHost((String) raw.get(FtpConfigKeys.HOST));
            cfg.setPort((Integer) raw.get(FtpConfigKeys.PORT));
            cfg.setUsername((String) raw.get(FtpConfigKeys.USERNAME));
            cfg.setPassword((String) raw.get(FtpConfigKeys.PASSWORD));

            Map<String, String> paths = new HashMap<>();
            Object pathObj = raw.get(FtpConfigKeys.PATHS);
            if (pathObj instanceof Map) {
                Map<String, Object> rawPaths = castMap(pathObj);
                for (Map.Entry<String, Object> pe : rawPaths.entrySet()) {
                    paths.put(pe.getKey(), String.valueOf(pe.getValue()));
                }
            }
            cfg.setPaths(paths);
            result.put(clientKey, cfg);
        }

        return result;
    }

    /**
     * <pre>
     * - 使用 castMap 方法做类型安全转换，便于维护；
     * </pre>
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object obj) {
        if (!(obj instanceof Map)) {
            throw new IllegalArgumentException("YAML 格式错误: 期望 Map 类型，但实际为 " + obj);
        }
        return (Map<String, Object>) obj;
    }


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

