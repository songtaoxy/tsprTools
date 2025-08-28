package com.st.modules.collections.list;
import java.util.List;
import java.util.Map;

/**
 * 工具类：集合与映射关系校验工具
 *
 * 功能清单：
 * 1. 判断 Map 的所有 key 是否都包含在 List 中
 *
 * 使用示例：
 *   List<String> list = Arrays.asList("a", "b", "c", "d");
 *   Map<String, Integer> map = new HashMap<>();
 *   map.put("a", 1);
 *   map.put("c", 2);
 *   boolean result = CollectionMapUtils.isMapKeysContainedInList(list, map);
 *   // result = true
 *
 * 注意事项：
 * - list 或 map 为空时，返回 false
 * - 支持泛型，但要求 List 元素类型与 Map key 类型一致
 */
public class ListIsOrNotContainsMapKey {
    /**
     * 判断 map 的所有 key 是否都在 list 中
     *
     * @param list  列表集合
     * @param map   映射集合
     * @param <T>   列表元素类型
     * @param <K>   map key 类型（需与 T 相同或兼容）
     * @param <V>   map value 类型
     * @return true: map 的所有 key 都包含在 list 中；false: 否则
     */
    public static <T, K extends T, V> boolean isMapKeysContainedInList(List<T> list, Map<K, V> map) {
        if (list == null || map == null || list.isEmpty() || map.isEmpty()) {
            return false;
        }
        return list.containsAll(map.keySet());
    }

}
