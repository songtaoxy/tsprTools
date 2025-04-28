package com.st.tools.common.redis;

import com.st.tools.common.redis.redisUtils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: st
 * @date: 2022/12/19 10:45
 * @version: 1.0
 * @description:
 */
@Slf4j
//@RestController
public class Demo10 {

	@Autowired
	RedisTemplate<String, String> redisTemplate;


	@Autowired
	private Redisson redisson;

	@GetMapping("/buygoods")
	public String buyGoods() {


		// ======================
		// keys and values
		// ======================
		String key = "good:001";
		String lock_key = "com.company.redis.lock";


		// ======================
		// get lock
		// ======================
		RedissonClient cli_79 = RedissonUtils.getCli_79();
		RedissonClient cli_89 = RedissonUtils.getCli_89();
		RedissonClient cli_99 = RedissonUtils.getCli_99();
		RLock lock79 = cli_79.getLock(lock_key);
		RLock lock89 = cli_89.getLock(lock_key);
		RLock lock99 = cli_99.getLock(lock_key);
		RedissonRedLock redlock = new RedissonRedLock(lock79, lock89, lock99);
		boolean isLocked;

		// ======================
		// result
		// ======================
		String result_success = "successful biz";
		String result_fail = "fail biz";

		try {
			isLocked = redlock.tryLock();

			if (isLocked) {
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
			}

		} finally {
			if (redlock.isLocked() && redlock.isHeldByCurrentThread()) {
				redlock.unlock();
			}
		}
		return result_fail;
	}
}
