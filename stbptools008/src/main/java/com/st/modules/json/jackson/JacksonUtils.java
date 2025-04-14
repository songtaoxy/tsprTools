package com.st.modules.json.jackson;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JacksonUtils {

    private static ObjectMapper mapper;

    static {
        mapper = createDefaultMapper();
    }

    /** 构建默认 ObjectMapper，支持 snake_case、Java 8 时间、自定义 Module */
    private static ObjectMapper createDefaultMapper() {
        ObjectMapper m = JsonMapper.builder()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .addModule(new JavaTimeModule())
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return m;
    }

    /** 注入自定义 ObjectMapper */
    public static void setMapper(ObjectMapper custom) {
        JacksonUtils.mapper = custom;
    }

    /** 注册扩展模块（如自定义序列化器） */
    public static void registerModule(Module module) {
        mapper.registerModule(module);
    }

    /** 设置字段命名策略（例如 SNAKE_CASE） */
    public static void setNamingStrategy(PropertyNamingStrategy strategy) {
        mapper.setPropertyNamingStrategy(strategy);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    public static String toPrettyJson(Object obj) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("格式化失败", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> ref) {
        try {
            return mapper.readValue(json, ref);
        } catch (IOException e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }

    public static JsonNode toJsonNode(Object obj) {
        return mapper.valueToTree(obj);
    }

    public static JsonNode parse(String json) {
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析失败", e);
        }
    }

    public static <T> T fromJsonFile(File file, Class<T> clazz) {
        try {
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            throw new RuntimeException("文件反序列化失败", e);
        }
    }

    public static void toJsonFile(File file, Object obj) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, obj);
        } catch (IOException e) {
            throw new RuntimeException("写入 JSON 文件失败", e);
        }
    }

    /** 使用 JsonView 控制序列化字段 */
    public static String toJsonWithView(Object obj, Class<?> view) {
        try {
            return mapper.writerWithView(view).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JsonView 序列化失败", e);
        }
    }

    /** JSON Patch：获取差异 */
    public static JsonNode diff(Object oldObj, Object newObj) {
        return JsonDiff.asJson(toJsonNode(oldObj), toJsonNode(newObj));
    }

    /** JSON Patch：应用补丁 */
    public static JsonNode applyPatch(JsonNode original, JsonNode patch) {
        try {
            return JsonPatch.apply(patch, original);
        } catch (Exception e) {
            throw new RuntimeException("Patch 应用失败", e);
        }
    }

    /** 多语言字段获取（默认 fallback） */
    public static String getLocalizedField(JsonNode node, String fieldPrefix, Locale locale) {
        String lang = locale.getLanguage();
        String key = fieldPrefix + "_" + lang;
        JsonNode val = node.get(key);
        if (val != null && !val.isNull()) return val.asText();

        // fallback 英文
        val = node.get(fieldPrefix + "_en");
        return val != null ? val.asText("") : "";
    }

    /** 生成 JSON Schema（需要启用 Jackson JSON Schema module） */
    public static JsonNode generateSchema(Class<?> clazz) {
        try {
            com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator schemaGen =
                    new com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator(mapper);
            com.fasterxml.jackson.module.jsonSchema.JsonSchema schema = schemaGen.generateSchema(clazz);
            return mapper.valueToTree(schema);
        } catch (Exception e) {
            throw new RuntimeException("生成 JSON Schema 失败", e);
        }
    }
}
