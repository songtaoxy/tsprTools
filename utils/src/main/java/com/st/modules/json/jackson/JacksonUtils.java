package com.st.modules.json.jackson;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * <li>一, 特点
 * <ul>线程安全（使用静态单例 ObjectMapper）</ul>
 *  <ul>支持泛型与 JSON 文件操作</ul>
 *   <ul>可读性强（含美化输出）</ul>
 *    <ul>扩展性强（模块注册、字段策略、@JsonView、Patch、Schema 全具备）</ul>
 *    <ul>支持 DTO/VO/Entity 等层次间深度转换（无须写 BeanUtils）</ul>
 * </li>
 * <li>二, 功能清单</li>
 * <li>基本要求: 使用泛型, 且通用</li>
 * <li>功能
 *     <ul>支持泛型对象的美化输出</ul>
 *     <ul>支持自定义字段序列化: 字段相同或不相同.</ul>
 *     <ul>支持 `@JsonView`</ul>
 *     <ul>自动注册 Java 8 时间模块</ul>
 *     <ul>支持 Java 8 时间类型格式化</ul>
 *     <ul>支持 JSON Patch / Diff（基于 zjsonpatch）</ul>
 *     <ul>支持 JSON Schema 生成</ul>
 *     <ul>使用统一字段命名策略（如 `snake_case`）</ul>
 *     <ul>支持本地化字段映射（可选）</ul>
 *     <ul>支持字段过滤（如 Jackson `@JsonView`）</ul>
 *     <ul>支持读写 JSON 文件</ul>
 *     <ul>JSON 对象/数组构建; 泛型构建 ObjectNode / ArrayNode: 支持任意 Bean、Map、List; 支持泛型对象初始化</ul>
 *     <ul>链式 JsonBuilder、ArrayBuilder: JsonBuilder 支持链式 put</ul>
 *     <ul>嵌套结构 & POJO 自动转换: putPOJO() 支持嵌套任意结构</ul>
 * </li>
 */
public class JacksonUtils {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 驼峰转成下划线
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());

        // 设置时间格式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 注册自定义模块（可选）
        SimpleModule customModule = new SimpleModule();
        // customModule.addSerializer(...);
        mapper.registerModule(customModule);
    }

    public static <T> String toJson(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    /**
     * <li>支持类型
     * <ul>- 字符串</ul>
     * </li>
     * @param obj
     * @return
     * @param <T>
     */
    public static <T> String toPrettyJson(T obj) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Pretty serialization failed", e);
        }
    }

    /**
     * 可复用上面的方法: {@com.st.modules.json.jackson.JacksonUtils#toPrettyJson(java.lang.Object)}
//     * @param node
     * @return
     */
    /*public static String toJsonPrettyString(JsonNode node) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to format JSON", e);
        }
    }*/

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return mapper.readValue(json, typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    public static <T> void toJsonFile(File file, T data) {
        try {
            mapper.writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException("Write to file failed", e);
        }
    }

    public static <T> T fromJsonFile(File file, Class<T> clazz) {
        try {
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Read from file failed", e);
        }
    }

    public static JsonNode toJsonNode(Object obj) {
        return mapper.valueToTree(obj);
    }

    public static JsonPatch diff(Object oldObj, Object newObj) {
        JsonNode source = toJsonNode(oldObj);
        JsonNode target = toJsonNode(newObj);
        return JsonDiff.asJsonPatch(source, target);
    }

    public static <T> T applyPatch(JsonPatch patch, T targetBean, Class<T> clazz) {
        try {
            JsonNode patched = patch.apply(toJsonNode(targetBean));
            return mapper.treeToValue(patched, clazz);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new RuntimeException("Apply patch failed", e);
        }
    }

    public static String generateSchema(Class<?> clazz) {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
                SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
        SchemaGenerator generator = new SchemaGenerator(configBuilder.build());
        JsonNode jsonSchema = generator.generateSchema(clazz);
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Generate schema failed", e);
        }
    }

    public static String toJsonView(Object obj, Class<?> viewClass) {
        try {
            return mapper.writerWithView(viewClass).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JsonView serialization failed", e);
        }
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    /**
     *
     * <li>泛型构建 ObjectNode / ArrayNode</li>
     * <li>链式 JsonBuilder、ArrayBuilder</li>
     * <li>嵌套结构 & POJO 自动转换</li>
     * <li>完整单元测试用例</li>
     * @return
     */
    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static <T> ObjectNode createObjectNode(T object) {
        return mapper.convertValue(object, ObjectNode.class);
    }

    public static ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }

    public static <T> ArrayNode createArrayNode(T object) {
        return mapper.convertValue(object, ArrayNode.class);
    }




    /**
     * 通用 Bean 转换
     * @param source 原始对象
     * @param targetClass 目标类类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) return null;
        return mapper.convertValue(source, targetClass);
    }

    /**
     * 通用 Bean 转换（支持复杂泛型类型）
     * @param source 原始对象
     * @param typeRef 目标类型引用
     * @return 转换后的对象
     */
    public static <T> T convertComplex(Object source, TypeReference<T> typeRef) {
        if (source == null) return null;
        return mapper.convertValue(source, typeRef);
    }

}

/**
 * 链式创建. 相见单元测试
 */
class JsonBuilder {
    private final ObjectNode root;

    private JsonBuilder() {
        this.root = JacksonUtils.getMapper().createObjectNode();
    }

    public static JsonBuilder create() {
        return new JsonBuilder();
    }

    public JsonBuilder put(String field, String value) {
        root.put(field, value);
        return this;
    }

    public JsonBuilder put(String field, int value) {
        root.put(field, value);
        return this;
    }

    public JsonBuilder put(String field, boolean value) {
        root.put(field, value);
        return this;
    }

    public JsonBuilder put(String field, double value) {
        root.put(field, value);
        return this;
    }

    public JsonBuilder put(String field, JsonNode node) {
        root.set(field, node);
        return this;
    }

    public JsonBuilder putPOJO(String field, Object value) {
        root.set(field, JacksonUtils.getMapper().valueToTree(value));
        return this;
    }

    public ObjectNode build() {
        return root;
    }
}

/**
 * 链式创建. 相见单元测试
 */
class ArrayBuilder {
    private final ArrayNode array;

    private ArrayBuilder() {
        this.array = JacksonUtils.getMapper().createArrayNode();
    }

    public static ArrayBuilder create() {
        return new ArrayBuilder();
    }

    public ArrayBuilder add(String value) {
        array.add(value);
        return this;
    }

    public ArrayBuilder add(int value) {
        array.add(value);
        return this;
    }

    public ArrayBuilder add(boolean value) {
        array.add(value);
        return this;
    }

    public ArrayBuilder add(double value) {
        array.add(value);
        return this;
    }

    public ArrayBuilder addPOJO(Object value) {
        array.add(JacksonUtils.getMapper().valueToTree(value));
        return this;
    }

    public ArrayBuilder add(JsonNode node) {
        array.add(node);
        return this;
    }

    public ArrayNode build() {
        return array;
    }
}