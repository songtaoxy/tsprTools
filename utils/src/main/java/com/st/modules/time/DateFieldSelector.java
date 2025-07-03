package com.st.modules.time;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * <pre>
 *     对 List<BO> 进行按某个 LocalDate 字段排序，并获取最大值对应的对象与最小值对应的对象
 *     - 方法支持泛型：可用于任何包含 LocalDate 字段的对象。
 *     - 通过 Function<T, LocalDate> 传入字段提取器（如 BO::getGldetailts）
 * </pre>
 *
 * Usage and test case(ok)
 * <pre>
 * {@code
 *      List<BO> list = Arrays.asList(
 *             new BO("A", LocalDate.of(2025, 1, 1)),
 *             new BO("B", LocalDate.of(2023, 6, 15)),
 *             new BO("C", LocalDate.of(2024, 3, 20))
 *         );
 *
 *         BO minBo = DateFieldSelector.getMinByDateField(list, BO::getGldetailts);
 *         BO maxBo = DateFieldSelector.getMaxByDateField(list, BO::getGldetailts);
 *
 *         System.out.println("最小日期对象: " + minBo); //最小日期对象: B - 2023-06-15
 *         System.out.println("最大日期对象: " + maxBo); //最大日期对象: A - 2025-01-01
 * }
 *
 *
 * <pre>扩展: 其他字段, 可以做类似扩展</pre>
 */
public class DateFieldSelector {

    /**
     * 获取某字段值最小的元素
     * @param list 数据列表
     * @param dateExtractor 字段提取函数（如 BO::getGldetailts）
     * @return 最小值对应的对象，若为空或无效返回 null
     */
    public static <T> T getMinByDateField(List<T> list, Function<T, LocalDate> dateExtractor) {
        if (list == null || list.isEmpty() || dateExtractor == null) return null;
        return list.stream()
                .filter(Objects::nonNull)
                .filter(e -> dateExtractor.apply(e) != null)
                .min(Comparator.comparing(dateExtractor))
                .orElse(null);
    }

    /**
     * 获取某字段值最大的元素
     * @param list 数据列表
     * @param dateExtractor 字段提取函数（如 BO::getGldetailts）
     * @return 最大值对应的对象，若为空或无效返回 null
     */
    public static <T> T getMaxByDateField(List<T> list, Function<T, LocalDate> dateExtractor) {
        if (list == null || list.isEmpty() || dateExtractor == null) return null;
        return list.stream()
                .filter(Objects::nonNull)
                .filter(e -> dateExtractor.apply(e) != null)
                .max(Comparator.comparing(dateExtractor))
                .orElse(null);
    }
}

