package com.st.modules.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <pre>
 * - 支持 users[0].name、users.0.name 两种路径风格
 * - 支持返回结果自动转换为常用类型：String、int、boolean、double
 * - 提供类型安全的封装方法：如 getIntByPath(...), getBooleanByPath(...)
 * - 错误路径容错，返回默认值而非抛异常
 * - 路径中支持混合对象、数组嵌套任意层
 * </pre>
 *
 * Usage and test; more detail see JsonPathUtils.md
 * <pre>
 *     {@code
 * ObjectMapper mapper = new ObjectMapper();
 * JsonNode root = mapper.readTree(jsonString);
 *
 * String name = JsonPathUtils.getTextByPath(root, "users[1].name", "未知");
 * Integer age = JsonPathUtils.getIntByPath(root, "users[0].age", 0);
 * Boolean active = JsonPathUtils.getBooleanByPath(root, "users[0].active", false);
 * Double score = JsonPathUtils.getDoubleByPath(root, "meta.score", 0.0);
 *     }
 * </pre>
 */
public class JsonPathUtils {

    public static JsonNode getByPath(JsonNode root, String path) {
        if (root == null || path == null || path.isEmpty()) return null;

        String[] parts = path.replaceAll("\\[(\\d+)]", ".$1").split("\\.");
        JsonNode current = root;

        for (String part : parts) {
            if (current == null || current.isMissingNode()) return null;

            if (part.matches("\\d+")) {
                if (current.isArray()) {
                    current = current.path(Integer.parseInt(part));
                } else {
                    return null;
                }
            } else {
                current = current.path(part);
            }
        }

        return current.isMissingNode() ? null : current;
    }

    public static String getTextByPath(JsonNode root, String path, String defaultValue) {
        JsonNode node = getByPath(root, path);
        return node != null && node.isValueNode() ? node.asText() : defaultValue;
    }

    public static Integer getIntByPath(JsonNode root, String path, Integer defaultValue) {
        JsonNode node = getByPath(root, path);
        return node != null && node.isInt() ? node.asInt() : defaultValue;
    }

    public static Double getDoubleByPath(JsonNode root, String path, Double defaultValue) {
        JsonNode node = getByPath(root, path);
        return node != null && node.isNumber() ? node.asDouble() : defaultValue;
    }

    public static Boolean getBooleanByPath(JsonNode root, String path, Boolean defaultValue) {
        JsonNode node = getByPath(root, path);
        return node != null && node.isBoolean() ? node.asBoolean() : defaultValue;
    }
}
