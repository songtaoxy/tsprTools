package com.st.modules.file.ftp2;

import java.util.*;

/**
 * 概述
 * FtpConfigRegistry 在应用启动时加载类路径配置并建立只读索引
 * 功能清单
 * 1 静态块按 appEnv 选择 config_{env}.yml 解析 ftp.servers
 * 2 getAllServers 返回全部实体
 * 3 getServerByCode 按 code 获取实体
 * 使用示例
 * List<FtpServer> all = FtpConfigRegistry.getAllServers();
 * FtpServer s = FtpConfigRegistry.getServerByCode("FGLS");
 * 注意事项
 * 仅承担配置到实体的加载与索引 不涉及连接
 * 入参与出参与异常说明
 * 缺字段或重复 code 抛 RuntimeException 未找到 code 抛 NoSuchElementException
 */
final class FtpConfigRegistry {
    private static volatile Map<String, FtpServer> BY_CODE = new LinkedHashMap<String, FtpServer>();
    private static volatile String ENV;
    private static volatile String RESOURCE;

    static {
        init();
    }

    private FtpConfigRegistry() {
    }

    public static void init() {
        String env = EnvResolver.resolveEnv();
        String res = EnvResolver.resolveClasspathResource(env);
        Map<String, Object> root = YamlLoader.loadFromClasspath(res);
        BY_CODE = parseServers(root, "ftp.servers");
        ENV = env;
        RESOURCE = res;
    }

    public static List<FtpServer> getAllServers() {
        return Collections.unmodifiableList(new ArrayList<FtpServer>(BY_CODE.values()));
    }

    public static FtpServer getServerByCode(String code) {
        FtpServer s = BY_CODE.get(code);
        if (s == null) throw new NoSuchElementException("FTP not found code=" + code);
        return s;
    }

    public static String env() {
        return ENV;
    }

    public static String resource() {
        return RESOURCE;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, FtpServer> parseServers(Map<String, Object> root, String path) {
        Object node = YamlLoader.getByPath(root, path);
        if (!(node instanceof List)) throw new RuntimeException("Path not a list " + path);
        List<?> arr = (List<?>) node;
        LinkedHashMap<String, FtpServer> map = new LinkedHashMap<String, FtpServer>();
        for (Object o : arr) {
            if (!(o instanceof Map)) throw new RuntimeException("Item not a map " + o);
            Map<String, Object> m = (Map<String, Object>) o;
            FtpServer s = new FtpServer();
            s.setCode(YamlLoader.reqStr(m, "code"));
            s.setName(YamlLoader.reqStr(m, "name"));
            s.setDesc(String.valueOf(m.getOrDefault("desc", "")));
            s.setHost(YamlLoader.reqStr(m, "host"));
            s.setPort(YamlLoader.reqInt(m, "port"));
            s.setUser(YamlLoader.reqStr(m, "user"));
            s.setPassword(YamlLoader.reqStr(m, "password"));
            s.setPassiveMode(YamlLoader.optBool(m, "passiveMode", false));
            s.setPath(YamlLoader.reqStr(m,"path"));
            if (map.containsKey(s.getCode())) throw new RuntimeException("Duplicate code " + s.getCode());
            map.put(s.getCode(), s);
        }
        return Collections.unmodifiableMap(map);
    }

}
