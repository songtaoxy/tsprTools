package com.st.tools.springbootweb.redis;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: st
 * @date: 2022/12/19 10:45
 * @version: 1.0
 * @description:
 */
@Slf4j
@RestController
public class Demo02 {

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	private final Lock lock = new ReentrantLock();

	@GetMapping("/buygoods")
	public  synchronized  String buyGoods() {

		/*
		lock.tryLock();
		lock.tryLock(2L, TimeUnit.SECONDS);
		*/

		String key = "good:001";
		String result_success = "successful biz";
		String result_fail = "fail biz";

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
	}
}
