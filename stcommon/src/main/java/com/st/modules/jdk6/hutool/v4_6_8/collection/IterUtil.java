package com.st.modules.jdk6.hutool.v4_6_8.collection;

import java.util.Iterator;

public class IterUtil {


    /**
     * 获取{@link Iterator}
     *
     * @param iterable {@link Iterable}
     * @param <T>      元素类型
     * @return 当iterable为null返回{@code null}，否则返回对应的{@link Iterator}
     * @since 5.7.2
     */
    public static <T> Iterator<T> getIter(Iterable<T> iterable) {
        return null == iterable ? null : iterable.iterator();
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> Iterator) {
        return null == Iterator || false == Iterator.hasNext();
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterable<?> iterable) {
        return null != iterable && isNotEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterator<?> Iterator) {
        return null != Iterator && Iterator.hasNext();
    }




}
