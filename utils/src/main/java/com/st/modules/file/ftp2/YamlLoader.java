package com.st.modules.file.ftp2;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class YamlLoader {

    private YamlLoader() {}
    /**
     * 概述
     * 从类路径读取 YAML 为 Map
     * 功能清单
     * 1 以 SafeConstructor 解析
     * 2 根节点必须为 Map
     * 使用示例
     * Map root = YamlLoader.loadFromClasspath("config_dev.yml");
     * 注意事项
     * 禁止将敏感值写入日志
     * 入参与出参与异常说明
     * 读取失败或结构错误抛 RuntimeException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadFromClasspath(String resource) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) throw new RuntimeException("Classpath resource not found " + resource);
            // 指定字符集,即使 Locale 或 file.encoding 不是 UTF-8，SnakeYAML 也能稳定解析
            // Object obj = new Yaml(new SafeConstructor()).load(new InputStreamReader(autoClose, StandardCharsets.UTF_8));
            Object obj = new Yaml(new SafeConstructor()).load(in);
            if (obj == null) return new LinkedHashMap<String, Object>();
            if (!(obj instanceof Map)) throw new RuntimeException("Root must be a map: " + resource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            throw new RuntimeException("Load YAML failed: " + resource, e);
        }
    }
    /** 点路径子树获取 */
    @SuppressWarnings("unchecked")
    public static Object getByPath(Map<String, Object> root, String path) {
        Object cur = root;
        for (String p : path.split("\\.")) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return cur;
    }
    /** 必填字符串 */
    public static String reqStr(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Empty " + k);
        return s;
    }
    /** 必填整型 */
    public static int reqInt(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) throw new IllegalArgumentException("Missing " + k);
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v).trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid int " + k + " val=" + v); }
    }
    /** 可选布尔 */
    public static boolean optBool(Map<String, Object> m, String k, boolean defVal) {
        Object v = m.get(k);
        if (v == null) return defVal;
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(s)) return true;
        if ("false".equals(s)) return false;
        throw new IllegalArgumentException("Invalid boolean " + k + " val=" + v);
    }
}
