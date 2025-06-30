package com.st.modules.string;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     - java中, 一个Xbo实体, 有很多个属性, 这些属性的类型都是String, 且长度有特定要求; 当前有一个list<Xbo>, 对list中的各个bo处理: 如果bo的某个属性的长度超过规定值, 从0截取规定的长度;
 *     - 即: 在 Java 中对一个 List<Xbo> 中的每个 Xbo 实例进行处理
 *     - 1, 若某个字段为 String 类型，
 *     - 2, 且长度超过该字段所设定的“最大长度限制”，
 *     - 3, 则将其值截断为规定的长度（value.substring(0, maxLen)）
 * </pre>
 */
public class StringLengthTrimmer {

    /**
     * <pre>
     * - 截断 List 中所有 Xbo 实例中字段值超过限制长度的字符串
     * - test: ok
     * </pre>
     * Usage and test
     * <pre>
     *     {@code
     *        @Test
     *     void trimStringFields() {
     *
     *         Xbo2 x1 = new Xbo2("张三", "longlonglongemail@example.com", "12345678901");
     *         Xbo2 x2 = new Xbo2("李四四四四", "user2@example.com", "987654321");
     *
     *         List<Xbo2> list = Arrays.asList(x1, x2);
     *
     *         Map<String, Integer> fieldMaxLens = new HashMap<>();
     *         fieldMaxLens.put("name", 4);
     *         fieldMaxLens.put("email", 10);
     *         fieldMaxLens.put("phone", 8);
     *
     *         StringLengthTrimmer.trimStringFields(list, fieldMaxLens);
     *
     *         // 验证结果
     *         for (Xbo2 x : list) {
     *             System.out.println(x.getName() + " | " + x.getEmail() + " | " + x.getPhone());
     *         }
     *     }
     * }
     * </pre>
     * and
     * <pre>
     *     {@code
     *     // Xbo2.java
     *     private String name;
     *     private String email;
     *     private String phone;
     *     }
     * </pre>
     * @param list         待处理的 Xbo 列表
     * @param fieldMaxLens 字段名 -> 最大长度 映射
     * @param <T>          实体类类型
     */
    public static <T> void trimStringFields(List<T> list, Map<String, Integer> fieldMaxLens) {
        if (list == null || list.isEmpty() || fieldMaxLens == null || fieldMaxLens.isEmpty()) return;

        for (T item : list) {
            if (item == null) continue;

            for (Map.Entry<String, Integer> entry : fieldMaxLens.entrySet()) {
                String fieldName = entry.getKey();
                int maxLen = entry.getValue();
                try {
                    Field field = item.getClass().getDeclaredField(fieldName);
                    if (field.getType() == String.class) {
                        field.setAccessible(true);
                        String value = (String) field.get(item);
                        if (value != null && value.length() > maxLen) {
                            field.set(item, value.substring(0, maxLen));
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // 可选: 记录日志、抛出异常，或者跳过该字段
                    throw new RuntimeException("字段处理出错: " + fieldName, e);
                }
            }
        }
    }
}

