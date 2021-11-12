package com.st.api.practice.guava;

import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import com.st.utils.log.LogUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author: st
 * @date: 2021/11/6 15:54
 * @version: 1.0
 * @description:
 */
@Slf4j
public class GuavaCache {


	@Test
	public void testCache() throws ExecutionException, InterruptedException {

		CacheLoader cacheLoader = new CacheLoader<String, Animal>() {
			// 如果找不到元素，会调用这里
			@Override
			public Animal load(String s) {
				//return null;
				return new Animal();
			}
		};
		LoadingCache<String, Animal> loadingCache = CacheBuilder.newBuilder()
				.maximumSize(1000) // 容量
				.expireAfterWrite(3, TimeUnit.SECONDS) // 过期时间
				.removalListener(new MyRemovalListener()) // 失效监听器
				.build(cacheLoader); //
		loadingCache.put("狗", new Animal("旺财", 1));
		loadingCache.put("猫", new Animal("汤姆", 3));
		loadingCache.put("狼", new Animal("灰太狼", 4));

		loadingCache.invalidate("猫"); // 手动失效

		Animal animal = loadingCache.get("狼");
		LogUtils.formatObjAndLogging(animal.toString(),"wolf in cache");
		Thread.sleep(4 * 1000);
		// 狼已经自动过去，获取为 null 值报错
		System.out.println(loadingCache.get("狼"));
		/**
		 * key=猫,value=Animal{name='汤姆', age=3},reason=EXPLICIT
		 * Animal{name='灰太狼', age=4}
		 * key=狗,value=Animal{name='旺财', age=1},reason=EXPIRED
		 * key=狼,value=Animal{name='灰太狼', age=4},reason=EXPIRED
		 *
		 * com.google.common.cache.CacheLoader$InvalidCacheLoadException: CacheLoader returned null for key 狼.
		 */
	}

	/**
	 * 缓存移除监听器
	 */
	class MyRemovalListener implements RemovalListener<String, Animal> {

		@Override
		public void onRemoval(RemovalNotification<String, Animal> notification) {
			String reason = String.format("key=%s,value=%s,reason=%s", notification.getKey(), notification.getValue(), notification.getCause());
			System.out.println(reason);
		}
	}

	class Animal {
		private String name;
		private Integer age;

		@Override
		public String toString() {
			return "Animal{" +
					"name='" + name + '\'' +
					", age=" + age +
					'}';
		}

		public Animal(String name, Integer age) {
			this.name = name;
			this.age = age;
		}

		public Animal(){

		}

	}
}
