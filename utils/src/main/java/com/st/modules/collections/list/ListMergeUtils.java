package com.st.modules.collections.list;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <pre>
 * 概述
 * 提供若干 List 合并方法，均对入参 list 及可变参数进行 null 安全处理；支持简单拼接、去重、按键去重、过滤合并与不可变返回
 * 功能清单
 * 1 merge 合并两个或多个 List 允许重复元素
 * 2 mergeDistinct 合并并去重 保留首次出现顺序
 * 3 mergeDistinctBy 合并并按 keyExtractor 定义的键去重
 * 4 mergeFiltered 按给定过滤条件合并
 * 5 mergeUnmodifiable 返回不可变 List
 * 使用示例
 *  List<String> a = Arrays.asList("A", "B", "B");
 *  List<String> b = Arrays.asList("B", "C");
 *  List<String> r1 = ListMergeUtils.merge(a, b); // [A, B, B, B, C]
 *  List<String> r2 = ListMergeUtils.mergeDistinct(a, b); // [A, B, C]
 *  List<String> r3 = ListMergeUtils.mergeDistinctBy((s) -> s.toLowerCase(), a, b); // 忽略大小写去重
 *  List<String> r4 = ListMergeUtils.mergeFiltered(x -> !"B".equals(x), a, b); // 过滤掉 B
 *  List<String> r5 = ListMergeUtils.mergeUnmodifiable(a, b); // 不可变结果
 * 注意事项
 * 1 所有方法均不会修改原始入参 返回全新结果
 * 2 当所有入参均为 null 或空时 返回空 List 而非 null
 * 3 mergeUnmodifiable 返回的集合不可修改 调用方若尝试修改将抛出 UnsupportedOperationException
 * 入参与出参说明
 *  入参 lists 可为 null 或包含 null 元素
 *  出参 为新的 ArrayList 或不可变 List 具体见方法说明
 * 异常说明
 *  无显式受检异常 对于 mergeUnmodifiable 若修改返回集合将抛 UnsupportedOperationException
 *  </pre>
 *
 *  <pre>
 *      应用场景:
 *      -  merge(List<T>... lists) 方法会自动跳过 null 或空列表，保证不会抛 NullPointerException。
 *      - mergeDistinct 用 LinkedHashSet 保证元素顺序且去重。
 *      - mergeDistinctBy 适合对象列表，例如 User::getId 去重。
 *      - mergeFiltered 适合需要条件筛选时使用。
 *      - mergeUnmodifiable 返回的结果不可修改，修改时会抛出 UnsupportedOperationException
 *  </pre>
 *  Usage
 *  <pre>
 *      {@code
 *          import java.util.Arrays;
 *          import java.util.List;
 *
 * public class ListMergeDemo {
 *     public static void main(String[] args) {
 *         List<String> list1 = Arrays.asList("A", "B", "B");
 *         List<String> list2 = Arrays.asList("B", "C");
 *         List<String> list3 = null; // 模拟 null
 *
 *         // 1. merge(List<T>... lists) 允许重复
 *         List<String> merged = ListMergeUtils.merge(list1, list2, list3);
 *         System.out.println("merge: " + merged);
 *         // 输出: [A, B, B, B, C]
 *
 *         // 2. mergeDistinct 去重 保留首次顺序
 *         List<String> mergedDistinct = ListMergeUtils.mergeDistinct(list1, list2);
 *         System.out.println("mergeDistinct: " + mergedDistinct);
 *         // 输出: [A, B, C]
 *
 *         // 3. mergeDistinctBy 按 key 去重（忽略大小写）
 *         List<String> mergedDistinctBy = ListMergeUtils.mergeDistinctBy(String::toLowerCase, list1, list2);
 *         System.out.println("mergeDistinctBy: " + mergedDistinctBy);
 *         // 输出: [A, B, C]
 *
 *         // 4. mergeFiltered 过滤合并（去掉 "B"）
 *         List<String> mergedFiltered = ListMergeUtils.mergeFiltered(s -> !"B".equals(s), list1, list2);
 *         System.out.println("mergeFiltered: " + mergedFiltered);
 *         // 输出: [A, C]
 *
 *         // 5. mergeUnmodifiable 返回不可变 List
 *         List<String> mergedUnmodifiable = ListMergeUtils.mergeUnmodifiable(list1, list2);
 *         System.out.println("mergeUnmodifiable: " + mergedUnmodifiable);
 *         // 输出: [A, B, B, B, C]
 *         // mergedUnmodifiable.add("X"); // 这里会抛 UnsupportedOperationException
 *     }
 * }
 *
 *      }
 *  </pre>
 */
public final class ListMergeUtils {

    private ListMergeUtils() { /* 工具类不允许实例化 */ }

    /**
     * 概述
     * 合并两个 List 允许重复 返回新列表
     * 功能
     * 1 忽略 null 入参
     * 2 维持元素原有相对顺序
     * 使用示例
     *  ListMergeUtils.merge(a, b)
     * 入参与出参
     *  @param a 第一个列表 可为 null
     *  @param b 第二个列表 可为 null
     *  @param <T> 元素类型
     *  @return 新的可变列表 若均为 null 返回空列表
     */
    public static <T> List<T> merge(List<T> a, List<T> b) {
        List<T> out = new ArrayList<>();
        if (a != null && !a.isEmpty()) out.addAll(a);
        if (b != null && !b.isEmpty()) out.addAll(b);
        return out;
    }

    /**
     * 概述
     * 合并多个 List 允许重复 返回新列表
     * 功能
     * 1 忽略 null 或空子列表
     * 2 维持各列表与元素的原始顺序
     * 使用示例
     *  ListMergeUtils.merge(a, b, c)
     * 入参与出参
     *  @param lists 可变参数列表 集合本身或其中任一元素可为 null
     *  @return 新的可变列表
     */
    @SafeVarargs
    public static <T> List<T> merge(List<T>... lists) {
        List<T> out = new ArrayList<>();
        if (lists == null || lists.length == 0) return out;
        for (List<T> l : lists) {
            if (l != null && !l.isEmpty()) out.addAll(l);
        }
        return out;
    }

    /**
     * <pre>
     * 概述
     * 合并并去重 使用 LinkedHashSet 保序
     * 功能
     * 1 保留首次出现顺序
     * 2 忽略 null 子列表
     * 使用示例
     *  ListMergeUtils.mergeDistinct(a, b)
     *  </pre>
     *
     *
     *  <pre>
     *  注意:
     *  - Java 中，如果直接对 null 调用 addAll 或 stream，会抛出 NullPointerException。因此需要先做判空处理
     *  </pre>
     *
     * 入参与出参
     *  @param lists 可变参数列表
     *  @return 去重后的可变列表
     */
    @SafeVarargs
    public static <T> List<T> mergeDistinct(List<T>... lists) {
        if (lists == null || lists.length == 0) return new ArrayList<>();
        Set<T> set = new LinkedHashSet<>();
        for (List<T> l : lists) {
            if (l != null && !l.isEmpty()) set.addAll(l);
        }
        return new ArrayList<>(set);
    }

    /**
     * 概述
     * 合并并按键去重 对于对象列表常用 例如按 id 去重
     * 功能
     * 1 通过 keyExtractor 生成键 保留每个键首次出现的元素
     * 2 维持整体遍历顺序
     * 使用示例
     *  class User { Long id; String name; }
     *  List<User> r = ListMergeUtils.mergeDistinctBy(User::getId, u1List, u2List)
     * 入参与出参
     *  @param keyExtractor 键提取函数 不可为 null
     *  @param lists 可变参数列表
     *  @return 去重后的可变列表
     * 异常
     *  若 keyExtractor 为 null 将抛出 NullPointerException
     */
    @SafeVarargs
    public static <T, K> List<T> mergeDistinctBy(Function<T, K> keyExtractor, List<T>... lists) {
        Objects.requireNonNull(keyExtractor, "keyExtractor");
        if (lists == null || lists.length == 0) return new ArrayList<>();
        List<T> out = new ArrayList<>();
        Set<K> seen = new HashSet<>();
        for (List<T> l : lists) {
            if (l == null || l.isEmpty()) continue;
            for (T t : l) {
                K k = keyExtractor.apply(t);
                // 允许键为 null 的场景 使用 seen.add 的返回值来决定是否首次出现
                if (seen.add(k)) {
                    out.add(t);
                }
            }
        }
        return out;
    }

    /**
     * 概述
     * 按给定过滤条件合并 允许重复
     * 功能
     * 1 在合并过程中应用 predicate 仅保留满足条件的元素
     * 使用示例
     *  ListMergeUtils.mergeFiltered(x -> x != null, a, b)
     * 入参与出参
     *  @param predicate 过滤条件 不可为 null
     *  @param lists 可变参数列表
     *  @return 过滤后的可变列表
     */
    @SafeVarargs
    public static <T> List<T> mergeFiltered(Predicate<T> predicate, List<T>... lists) {
        Objects.requireNonNull(predicate, "predicate");
        List<T> out = new ArrayList<>();
        if (lists == null || lists.length == 0) return out;
        for (List<T> l : lists) {
            if (l == null || l.isEmpty()) continue;
            for (T t : l) {
                if (predicate.test(t)) out.add(t);
            }
        }
        return out;
    }

    /**
     * 概述
     * 合并并返回不可变结果 允许重复
     * 功能
     * 1 返回的 List 不可修改
     * 使用示例
     *  List<String> r = ListMergeUtils.mergeUnmodifiable(a, b)
     * 入参与出参
     *  @param lists 可变参数列表
     *  @return 不可变 List
     * 异常
     *  对返回结果进行修改将抛 UnsupportedOperationException
     */
    @SafeVarargs
    public static <T> List<T> mergeUnmodifiable(List<T>... lists) {
        List<T> merged = merge(lists);
        return Collections.unmodifiableList(merged);
    }
}

