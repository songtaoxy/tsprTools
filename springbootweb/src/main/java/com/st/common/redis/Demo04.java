package com.st.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author: st
 * @date: 2022/12/19 10:45
 * @version: 1.0
 * @description:
 */
@Slf4j
//@RestController
public class Demo04 {

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
			Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lock_key, lock_value);
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
