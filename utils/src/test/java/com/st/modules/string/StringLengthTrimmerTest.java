package com.st.modules.string;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StringLengthTrimmerTest {

    @Test
    void trimStringFields() {

        Xbo2 x1 = new Xbo2("张三", "longlonglongemail@example.com", "12345678901");
        Xbo2 x2 = new Xbo2("李四四四四", "user2@example.com", "987654321");

        List<Xbo2> list = Arrays.asList(x1, x2);

        Map<String, Integer> fieldMaxLens = new HashMap<>();
        fieldMaxLens.put("name", 4);
        fieldMaxLens.put("email", 10);
        fieldMaxLens.put("phone", 8);

        StringLengthTrimmer.trimStringFields(list, fieldMaxLens);

        // 验证结果
        for (Xbo2 x : list) {
            System.out.println(x.getName() + " | " + x.getEmail() + " | " + x.getPhone());
        }
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class Xbo2 {
    private String name;
    private String email;
    private String phone;
}
