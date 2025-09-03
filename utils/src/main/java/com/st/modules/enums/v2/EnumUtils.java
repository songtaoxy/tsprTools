package com.st.modules.enums.v2;

import com.st.modules.enums.v1.BaseEnum;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class EnumUtils {


        private static Map<Class, Object> map = new ConcurrentHashMap<>();
        private static final Map<Class<?>, Object[]> CACHE = new ConcurrentHashMap<>();


    /**
     * <li>通用：根据条件获取枚举对象</li>
     * <li>测试案例, ref {@code com.st.modules.enums.EnumUtilsTest#getEnumObjectV2()}</li>
     */
    public static <T> Optional<T> getEnumObject(Class<T> clazz, Predicate<T> predicate) {
        if (!clazz.isEnum()) return Optional.empty();

        Object[] values = CACHE.computeIfAbsent(clazz, c -> clazz.getEnumConstants());
        return Arrays.stream((T[]) values).filter(predicate).findFirst();
    }

    /**
     * 通用：根据 code 获取枚举
     */
    public static <E extends Enum<E> & BaseEnum> Optional<E> getByCode(Class<E> clazz, String code) {
        return getEnumObject(clazz, e -> e.getCode().equals(code));
    }

    /**
     * 通用：根据 name 获取枚举
     */
    public static <E extends Enum<E> & BaseEnum> Optional<E> getByName(Class<E> clazz, String name) {
        return getEnumObject(clazz, e -> e.getName().equals(name));
    }

    /**
     * 通用：根据 name 获取 code
     */
    public static <E extends Enum<E> & BaseEnum> String getCodeByName(Class<E> clazz, String name) {
        return getByName(clazz, name).map(e->e.getCode()).orElse(null);
    }

    /**
     * 通用：根据 code 获取 name
     */
    public static <E extends Enum<E> & BaseEnum> String getNameByCode(Class<E> clazz, String code) {
        return getByCode(clazz, code).map(e->e.getName()).orElse(null);
    }



    /**
     * 根据条件获取枚举对象
     *  <pre>
     *      <li>
     *          已经废弃. 最新,ref {@link EnumUtils#getEnumObject(Class, Predicate)}
     *      </li>
     *  </pre>
     * @param className 枚举类
     * @param predicate 筛选条件
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getEnumObjectV1(Class<T> className, Predicate<T> predicate) {

        if (!className.isEnum()) {
            return null;
        }

        Object obj = map.get(className);

        T[] ts = null;

        if (obj == null) {
            ts = className.getEnumConstants();
            map.put(className, ts);
        } else {
            ts = (T[]) obj;
        }


        return Arrays.stream(ts).filter(predicate).findAny();
    }



}

