package com.st.modules.string;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NullToEmptyStringUtilsTest {

    @Test
    void convertNullStringsToEmpty() {
        Xbo bo1 = new Xbo(null, "user1@example.com", null);
        Xbo bo2 = new Xbo("Alice", null, "123456");
        Xbo bo3 = new Xbo(null, null, null);

        List<Xbo> list = Arrays.asList(bo1, bo2, bo3);

        NullToEmptyStringUtils.convertNullStringsToEmpty(list);

        Assertions.assertEquals("", bo1.getName());
        Assertions.assertEquals("user1@example.com", bo1.getEmail());
        Assertions.assertEquals("", bo1.getPhone());

        Assertions.assertEquals("Alice", bo2.getName());
        Assertions.assertEquals("", bo2.getEmail());
        Assertions.assertEquals("123456", bo2.getPhone());

        Assertions.assertEquals("", bo3.getName());
        Assertions.assertEquals("", bo3.getEmail());
        Assertions.assertEquals("", bo3.getPhone());

    }

    @Test
    void convertNullValuesToEmpty() {


        Map<String, String> map1 = new HashMap<>();
        map1.put("name", null);
        map1.put("email", "user1@example.com");

        Map<String, String> map2 = new HashMap<>();
        map2.put("name", "Alice");
        map2.put("email", null);

        Map<String, String> map3 = new HashMap<>();
        map3.put("name", null);
        map3.put("email", null);

        List<Map<String, String>> list = Arrays.asList(map1, map2, map3);

        NullToEmptyStringUtils.convertNullValuesToEmpty(list);

        Assertions.assertEquals("", map1.get("name"));
        Assertions.assertEquals("user1@example.com", map1.get("email"));

        Assertions.assertEquals("Alice", map2.get("name"));
        Assertions.assertEquals("", map2.get("email"));

        Assertions.assertEquals("", map3.get("name"));
        Assertions.assertEquals("", map3.get("email"));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class Xbo {
        private String name;
        private String email;
        private String phone;

        // getter/setter 省略
    }

}