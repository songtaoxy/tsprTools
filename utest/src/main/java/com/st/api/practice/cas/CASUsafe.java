package com.st.api.practice.cas;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author: st
 * @date: 2021/7/10 14:01
 * @version: 1.0
 * @description:
 */
public class CASUsafe {

	public static void main(String[] args) throws Exception {
		Unsafe unsafe = getUnsafe();
		long offset = unsafe.objectFieldOffset(Entry.class.getDeclaredField("id"));
		System.out.println("offset: " + offset);
		Entry entry = new Entry();
		entry.setId(1);
		boolean b = unsafe.compareAndSwapInt(entry, offset, 1, 2);
		System.out.println(b);
		System.out.println(entry.getId());
	}

	/**
	 * 通过反射获取Unsafe实例
	 */
	public static Unsafe getUnsafe() throws IllegalAccessException, NoSuchFieldException {
		Field field = Unsafe.class.getDeclaredField("theUnsafe");
		field.setAccessible(true);
		return (Unsafe)field.get(null);
	}

	public static class Entry{
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}
}
