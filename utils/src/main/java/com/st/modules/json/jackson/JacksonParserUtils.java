package com.st.modules.json.jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;

/**
 * <pre>
 *     - Jackson读取json string, 然后解析各数据
 *     - test case: ok
 *
 * </pre>
 *
 * Usage and case
 * <pre>
 *     {@code
 *          String json = "{ \"name\": \"Alice\", \"age\": 25, \"address\": { \"city\": \"NY\" }, \"tags\": [\"a\", \"b\"] }";
 *
 *         String name = JacksonParserUtils.getString(json, "name", "unknown");
 *         int age = JacksonParserUtils.getInt(json, "age", 0);
 *         String city = JacksonParserUtils.getString(json, "address.city", "none");
 *         List<String> tags = JacksonParserUtils.getStringList(json, "tags");
 *
 *
 *         JsonNode parse = JacksonParserUtils.parse(json);
 *         String prettyJson = JacksonUtils.toPrettyJson(parse);
 *         System.out.println(prettyJson);
 *         System.out.println("=========================");
 *
 *
 *         System.out.println("name: " + name);  // Alice
 *         System.out.println("age: " + age);   // 25
 *         System.out.println("city: " + city);  // NY
 *         System.out.println("tags: " + tags);  // [a, b]
 *     }
 * </pre>
 *
 * input & output
 * <pre>
 *     {@code
 *
 *          {
 *   "name" : "Alice",
 *   "age" : 25,
 *   "address" : {
 *     "city" : "NY"
 *   },
 *   "tags" : [ "a", "b" ]
 * }
 * =========================
 * name: Alice
 * age: 25
 * city: NY
 * tags: [a, b]
 *
 *     }
 * </pre>
 *
 */
public class JacksonParserUtils {
    private static final ObjectMapper mapper = JacksonUtils.getMapper();

    /**
     * 将 JSON 字符串解析为 JsonNode
     */
    public static JsonNode parse(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON", e);
        }
    }

    /**
     * 根据路径获取 JsonNode
     * 路径支持多级，例如: "address.city"
     */
    private static JsonNode getNode(JsonNode root, String path) {
        String[] parts = path.split("\\.");
        JsonNode current = root;
        for (String part : parts) {
            if (current == null || current.isMissingNode()) return null;
            current = current.get(part);
        }
        return current;
    }

    public static String getString(String json, String path, String defaultValue) {
        JsonNode node = getNode(parse(json), path);
        return (node != null && !node.isNull()) ? node.asText() : defaultValue;
    }

    public static int getInt(String json, String path, int defaultValue) {
        JsonNode node = getNode(parse(json), path);
        return (node != null && node.isInt()) ? node.asInt() : defaultValue;
    }

    public static boolean getBoolean(String json, String path, boolean defaultValue) {
        JsonNode node = getNode(parse(json), path);
        return (node != null && node.isBoolean()) ? node.asBoolean() : defaultValue;
    }

    public static double getDouble(String json, String path, double defaultValue) {
        JsonNode node = getNode(parse(json), path);
        return (node != null && node.isNumber()) ? node.asDouble() : defaultValue;
    }

    public static List<String> getStringList(String json, String path) {
        JsonNode arrayNode = getNode(parse(json), path);
        List<String> list = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            for (JsonNode element : arrayNode) {
                list.add(element.asText());
            }
        }
        return list;
    }

    public static <T> T getObject(String json, String path, Class<T> clazz) {
        JsonNode node = getNode(parse(json), path);
        try {
            return (node != null && !node.isNull()) ? mapper.treeToValue(node, clazz) : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert node to object", e);
        }
    }

    public static <T> T getObject(String json, String path, TypeReference<T> typeRef) {
        JsonNode node = getNode(parse(json), path);
        try {
            return (node != null && !node.isNull()) ? mapper.convertValue(node, typeRef) : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert node to generic object", e);
        }
    }

}

