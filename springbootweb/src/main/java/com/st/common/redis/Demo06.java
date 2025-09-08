package com.st.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: st
 * @date: 2022/12/19 10:45
 * @version: 1.0
 * @description:
 */
@Slf4j
//@RestController
public class Demo06 {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@GetMapping("/buygoods")
	public String buyGoods() {


		// ======================
		// keys and values
		// ======================
		String key = "good:001";
		String lock_key = "com.company.redis.lock";

		// ======================
		// result
		// ======================
		String result_success = "successful biz";
		String result_fail = "fail biz";
		String getLock = "fail go get lock";

		try {
			// ======================
			// get lock
			// ======================
			String lock_value = UUID.randomUUID().toString() + Thread.currentThread().getName();
			/*
			Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lock_key, lock_value);
			// 过期, 防死锁
			redisTemplate.expire(lock_key, 10L, TimeUnit.SECONDS);
			*/
			Boolean aBoolean=redisTemplate.opsForValue().setIfAbsent(lock_key, lock_value,10L,TimeUnit.SECONDS);

			if (Boolean.FALSE.equals(aBoolean)) {
				return getLock;
			}


			// ======================
			// biz
			// ======================
			String value = redisTemplate.opsForValue().get(key);
			int num = value == null ? 0 : Integer.parseInt(value);
			if (num > 0) {
				Integer rest = num - 1;
				redisTemplate.opsForValue().set(key, rest + "");
				log.info(result_success);


				return result_success;
			} else {

				return result_fail;
			}

		} finally {
			// release lock
			redisTemplate.delete(lock_key);
		}
	}
}
