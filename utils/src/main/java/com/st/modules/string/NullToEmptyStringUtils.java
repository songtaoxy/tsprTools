package com.st.modules.string;

import java.lang.reflect.Field;
import java.util.List;

/**
 * <pre>
 * - 使用 Java 的 反射 来通用处理：对 List<Xbo> 中的每个对象，如果属性为 null 且属性类型为 String，就将其赋值为空字符串 ""
 * - test: ok
 * </pre>
 *
 * Usage:
 * <pre>
 *     {@code
 *          List<Xbo> list = ... // 初始化你的列表
 *          NullToEmptyStringUtil.convertNullStringsToEmpty(list);
 *     }
 * </pre>
 * 注意事项:
 * <pre>
 * - 如果属性是 父类中声明的字段，需要递归获取父类字段，可改进 getDeclaredFields() 的逻辑；
 * - 如果你使用的是 Lombok，确保使用 @Accessors(chain = true) 不会影响反射访问；
 * - 如果字段为 private 且启用了安全管理器（如某些安全沙箱），setAccessible(true) 可能被限制；
 * - 不适用于基本类型或 String[] 这样的数组类型属性；
 * - 如果你希望排除某些字段，也可以加白名单或注解过滤
 * </pre>
 */
public class NullToEmptyStringUtils {

    public static <T> void convertNullStringsToEmpty(List<T> list) {
        if (list == null || list.isEmpty()) return;

        for (T item : list) {
            if (item == null) continue;

            Field[] fields = item.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(item);
                        if (value == null) {
                            field.set(item, "");
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("处理字段出错: " + field.getName(), e);
                    }
                }
            }
        }
    }
}

