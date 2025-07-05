package com.st.modules.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonPathUtilsTest {

    String jsonString = "{\n" +
            "  \"user\": {\n" +
            "    \"name\": \"张三\",\n" +
            "    \"age\": 30,\n" +
            "    \"active\": true,\n" +
            "    \"score\": 88.5,\n" +
            "    \"address\": {\n" +
            "      \"city\": \"北京\",\n" +
            "      \"zip\": null\n" +
            "    }\n" +
            "  },\n" +
            "  \"users\": [\n" +
            "    {\n" +
            "      \"name\": \"李四\",\n" +
            "      \"age\": 28,\n" +
            "      \"active\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"王五\",\n" +
            "      \"address\": {\n" +
            "        \"city\": \"上海\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"meta\": {\n" +
            "    \"tags\": [\"开发\", \"测试\"],\n" +
            "    \"emptyArray\": [],\n" +
            "    \"emptyObject\": {}\n" +
            "  }\n" +
            "}\n";

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root;

    {
        try {
            root = mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSimpleStringPath() {
        assertEquals("张三", JsonPathUtils.getTextByPath(root, "user.name", null));
    }

    @Test
    public void testNestedArrayObject() {
        assertEquals("上海", JsonPathUtils.getTextByPath(root, "users[1].address.city", null));
    }

    @Test
    public void testInvalidArrayIndex() {
        assertNull(JsonPathUtils.getTextByPath(root, "users[10].name", null));
    }

    @Test
    public void testNonExistingPath() {
        assertNull(JsonPathUtils.getTextByPath(root, "user.nonexistent.key", null));
    }

    @Test
    public void testArrayStylePathCompatible() {
        assertEquals("李四", JsonPathUtils.getTextByPath(root, "users.0.name", null));
    }

    @Test
    public void testDefaultValueFallback() {
        assertEquals("默认", JsonPathUtils.getTextByPath(root, "user.unknown", "默认"));
    }

    @Test
    public void testGetBoolean() {
        assertTrue(JsonPathUtils.getBooleanByPath(root, "user.active", false));
    }

    @Test
    public void testGetDouble() {
        assertEquals(88.5, JsonPathUtils.getDoubleByPath(root, "user.score", 0.0), 0.01);
    }

}