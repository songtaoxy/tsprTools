package com.st.modules.string;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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