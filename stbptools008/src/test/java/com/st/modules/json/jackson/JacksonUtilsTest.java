package com.st.modules.json.jackson;


import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonUtilsTest {

    static class User {
        public String name;
        public int age;
        public LocalDate birthday;

        public User() {}
        public User(String name, int age, LocalDate birthday) {
            this.name = name;
            this.age = age;
            this.birthday = birthday;
        }
    }

    @Test
    public void testToJsonAndFromJson() {
        User user = new User("Alice", 30, LocalDate.of(1993, 4, 1));
        String json = JacksonUtils.toJson(user);
        assertTrue(json.contains("alice") || json.contains("Alice"));

        User parsed = JacksonUtils.fromJson(json, User.class);
        assertEquals("Alice", parsed.name);
        assertEquals(30, parsed.age);
        assertEquals(LocalDate.of(1993, 4, 1), parsed.birthday);
    }

    @Test
    public void testToJsonNodeAndParse() {
        User user = new User("Bob", 25, LocalDate.of(1999, 1, 1));
        JsonNode node = JacksonUtils.toJsonNode(user);
        assertEquals("Bob", node.get("name").asText());
        assertEquals(25, node.get("age").asInt());

        String json = JacksonUtils.toJson(user);
        JsonNode parsed = JacksonUtils.parse(json);
        assertEquals("Bob", parsed.get("name").asText());
    }

    @Test
    public void testDiffAndPatch() {
        User u1 = new User("Alice", 30, LocalDate.of(1993, 4, 1));
        User u2 = new User("Alice", 31, LocalDate.of(1993, 4, 1));

        JsonNode patch = JacksonUtils.diff(u1, u2);
        assertTrue(patch.toString().contains("/age"));

        JsonNode original = JacksonUtils.toJsonNode(u1);
        JsonNode updated = JacksonUtils.applyPatch(original, patch);
        assertEquals(31, updated.get("age").asInt());
    }

    @Test
    public void testNamingStrategySnakeCase() {
        JacksonUtils.setNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);

        class CamelUser {
            public String userName = "Tom";
        }

        String json = JacksonUtils.toJson(new CamelUser());
        assertTrue(json.contains("user_name"));
    }

    @Test
    public void testJsonSchemaGeneration() {
        JsonNode schema = JacksonUtils.generateSchema(User.class);
        assertNotNull(schema.get("properties"));
        assertTrue(schema.toPrettyString().contains("name"));
    }

    @Test
    public void testPrettyPrint() {
        User user = new User("Alice", 30, LocalDate.of(1993, 4, 1));
        String pretty = JacksonUtils.toPrettyJson(user);
        assertTrue(pretty.contains("\n"));
    }
}
