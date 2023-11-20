package com.st.modules.enums.common;

import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2023/11/9 09:13
 * @version: 1.0
 * @description:
 */
public class EnumUtils {

	/**
	 * 指定类是否为Enum类
	 *
	 * @param clazz 类
	 * @return 是否为Enum类
	 */
	public static boolean isEnum(Class<?> clazz) {
		//Assert.notNull(clazz);
		Preconditions.checkNotNull(clazz, "can't be null");
		return clazz.isEnum();
	}

	/**
	 * 指定类是否为Enum类
	 *
	 * @param obj 类
	 * @return 是否为Enum类
	 */
	public static boolean isEnum(Object obj) {
		//Assert.notNull(obj);
		Preconditions.checkNotNull(obj, "can't be null");
		return obj.getClass().isEnum();
	}


	/**
	 * 获取所有枚举项
	 *
	 * @param enumType 枚举类型
	 * @param <T>      EnumItem
	 * @return 枚举项列表
	 */
	public static <T extends EnumItem<?>> List<T> getEnumItems(Class<T> enumType) {
		return Arrays.asList(enumType.getEnumConstants());
	}

	/**
	 * 获取key为code的map
	 *
	 * @param enumType 枚举类型
	 * @param <V>      code泛型
	 * @param <T>      EnumItem
	 * @return
	 */
	public static <V, T extends EnumItem<V>> Map<V, String> getEnumMapCode(Class<T> enumType) {
		Map<V, String> map = new HashMap<>();
		for (T enumItem : enumType.getEnumConstants()) {
			map.put(enumItem.getCode(), enumItem.getValue());
		}
		return map;
	}

	/**
	 * 获取key为value的map
	 *
	 * @param enumType 枚举类型
	 * @param <V>      code泛型
	 * @param <T>      EnumItem
	 * @return
	 */
	public static <V, T extends EnumItem<V>> Map<String, V> getEnumMapValue(Class<T> enumType) {
		Map<String, V> map = new HashMap<>();
		for (T enumItem : enumType.getEnumConstants()) {
			map.put(enumItem.getValue(), enumItem.getCode());
		}
		return map;
	}

	/**
	 * 获取枚举所有项的列表
	 *
	 * @param enumType 枚举类型
	 * @param <T>      EnumItem
	 * @return
	 */
	public static <T extends EnumItem<?>> List<Map<String, Object>> getEnumList(Class<T> enumType) {
		List<Map<String, Object>> list = new ArrayList<>();
		if (!enumType.isEnum()) {
			return new ArrayList<>();
		}
		T[] enums = enumType.getEnumConstants();
		if (enums == null || enums.length <= 0) {
			return new ArrayList<>();
		}
		for (T enumItem : enums) {
			Map<String, Object> map = new HashMap<>();
			map.put("code", enumItem.getCode());
			map.put("value", enumItem.getValue());
			map.put("name", enumItem.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 获取枚举的所有Code值
	 *
	 * @param enumType 枚举类型
	 * @param <T>      EnumItem
	 * @param <V>      code泛型
	 * @return 枚举code值列表
	 */
	public static <V, T extends EnumItem<V>> List<V> getEnumCodes(Class<T> enumType) {
		return Arrays.stream(enumType.getEnumConstants()).map(EnumItem::getCode).collect(Collectors.toList());
	}

	/**
	 * 获取枚举的所有Value值
	 *
	 * @param enumType 枚举类型
	 * @param <T>      EnumItem
	 * @return 枚举value值列表
	 */
	public static <T extends EnumItem<?>> List<String> getEnumValues(Class<T> enumType) {
		return Arrays.stream(enumType.getEnumConstants()).map(EnumItem::getValue).collect(Collectors.toList());
	}

	/**
	 * 根据Code值查询对应的枚举项
	 *
	 * @param enumType 枚举类型
	 * @param code     枚举code
	 * @param <T>      EnumItem
	 * @param <V>      code泛型
	 * @return 枚举项
	 */
	public static <V, T extends EnumItem<V>> T fromCode(Class<T> enumType, V code) {
		if (code == null) {
			return null;
		}
		for (T enumItem : enumType.getEnumConstants()) {
			if (equals(code, enumItem.getCode())) {
				return enumItem;
			}
		}
		return null;

	}



	/**
	 * 根据value获取对应的枚举项
	 *
	 * @param enumType
	 * @param value
	 * @param <T>
	 * @return
	 */
	public static <T extends EnumItem<?>> T fromValue(Class<T> enumType, String value) {
		if (value == null) {
			return null;
		}
		for (T enumItem : enumType.getEnumConstants()) {
			if (equals(value, enumItem.getValue())) {
				return enumItem;
			}
		}
		return null;

	}

	/**
	 * 根据Name获取对应的枚举项
	 *
	 * @param enumType
	 * @param value
	 * @param <T>
	 * @return
	 */
	public static <T extends EnumItem<?>> T fromName(Class<T> enumType, String name) {
		if (name == null) {
			return null;
		}
		for (T enumItem : enumType.getEnumConstants()) {
			if (equals(name, enumItem.getName())) {
				return enumItem;
			}
		}
		return null;

	}

	/**
	 * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}
	 * <ul>
	 *     根据枚举的名字, 获取枚举
	 *     <li>比如, 下面的枚举, 传入的是 "MALE", 得到 MALE这个枚举类 </li>
	 *     <li>和上面的方法{@link #fromName(Class, String)}的区别是:fromName方法传的是name字段的值</li>
	 * </ul>
	 * <pre>
	 *     {@code
	 *
	 *     public enum SexEnum implements EnumItem<String> {
	 *
	 *     //实现EnumItem接口，并指定该枚举code的数据类型
	 *     MALE("0", "男","","",""),
	 *     FEMALE("1", "女","","","");
	 *
	 *
	 *     private final String code;
	 *     private final String name;
	 *     private final String key;
	 *     private final String value;
	 *     private final String des;
	 *
	 *     }
	 * </pre>
	 *
	 * @param <E>       枚举类型泛型
	 * @param enumClass 枚举类
	 * @param value     值
	 * @return 枚举值
	 * @since 4.1.13
	 */
	public static <E extends Enum<E>> E fromEnumName(Class<E> enumClass, String name) {
		return Enum.valueOf(enumClass, name);
	}

	public static boolean  equals(Object a, Object b) {
		return (a == b) || (a != null && a.equals(b));
	}

}
