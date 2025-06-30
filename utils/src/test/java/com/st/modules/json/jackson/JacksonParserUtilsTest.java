package com.st.modules.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JacksonParserUtilsTest {

    @Test
    void parse() {

        String json = "{ \"name\": \"Alice\", \"age\": 25, \"address\": { \"city\": \"NY\" }, \"tags\": [\"a\", \"b\"] }";

        String name = JacksonParserUtils.getString(json, "name", "unknown");
        int age = JacksonParserUtils.getInt(json, "age", 0);
        String city = JacksonParserUtils.getString(json, "address.city", "none");
        List<String> tags = JacksonParserUtils.getStringList(json, "tags");


        JsonNode parse = JacksonParserUtils.parse(json);
        String prettyJson = JacksonUtils.toPrettyJson(parse);
        System.out.println(prettyJson);
        System.out.println("=========================");


        System.out.println("name: " + name);  // Alice
        System.out.println("age: " + age);   // 25
        System.out.println("city: " + city);  // NY
        System.out.println("tags: " + tags);  // [a, b]

    }
}
