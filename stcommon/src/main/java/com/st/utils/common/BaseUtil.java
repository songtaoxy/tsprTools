package com.st.utils.common;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.util.Objects;

public class BaseUtil {



    /**
     * 检查对象是否为null<br>
     * 判断标准为：
     *
     * <pre>
     * 1. == null
     * 2. equals(null)
     * </pre>
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNull(Object obj) {
        //noinspection ConstantConditions
        return null == obj || obj.equals(null);
    }

    /**
     * 检查对象是否不为null
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNotNull(Object obj) {
        return false == isNull(obj);
    }




    /**
     * 比较两个对象是否相等<br>
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
     * </ol>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     *
     * @since 2023.11.12
     */
    public static boolean equals(Object obj1, Object obj2) {

        if (obj1 instanceof BigDecimal && obj2 instanceof BigDecimal) {
            return NumberUtil.equals((BigDecimal) obj1, (BigDecimal) obj2);
        }
        return (obj1 == obj2) || (obj1 != null && obj1.equals(obj2));
    }


    /**
     * 比较两个对象是否不相等。<br>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否不等
     * @since 3.0.7
     */
    public static boolean notEqual(Object obj1, Object obj2) {
        return !equals(obj1, obj2);
    }

}
