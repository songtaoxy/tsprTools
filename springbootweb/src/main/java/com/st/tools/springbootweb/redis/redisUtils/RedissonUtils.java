package com.st.tools.springbootweb.redis.redisUtils;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author: st
 * @date: 2022/12/19 13:21
 * @version: 1.0
 * @description:
 */
public class RedissonUtils {

	private static RedissonClient cli_79;
	private static RedissonClient cli_89;
	private static RedissonClient cli_99;

	static {
		Config config_79 = new Config();
		config_79.useSingleServer()
				.setAddress("127.0.0.1:6379") // 注意这里我的Redis测试实例没密码
				.setDatabase(0);
		cli_79 = Redisson.create(config_79);

		Config config_89 = new Config();
		config_89.useSingleServer()
				.setAddress("127.0.0.1:6389")
				.setDatabase(0);
		cli_89 = Redisson.create(config_89);

		Config config_99 = new Config();
		config_99.useSingleServer()
				.setAddress("127.0.0.1:6399")
				.setDatabase(0);
		cli_99 = Redisson.create(config_99);
	}

	public static RedissonClient getCli_79() {
		return cli_79;
	}
	public static RedissonClient getCli_89() {
		return cli_89;
	}

	public static RedissonClient getCli_99() {
		return cli_99;
	}

}
