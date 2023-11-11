package com.st.utils.enums.case01;

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
			if (Objects.equals(code, enumItem.getCode())) {
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
			if (Objects.equals(value, enumItem.getValue())) {
				return enumItem;
			}
		}
		return null;

	}

}
