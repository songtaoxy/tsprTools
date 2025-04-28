package com.st.tools.common.redis.redisUtils;

import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author: st
 * @date: 2022/12/19 12:42
 * @version: 1.0
 * @description:
 */
public class RedisUtils {
	private static final JedisPool jedisPool;

	static {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(20);
		jedisPoolConfig.setMaxIdle(10);
		jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6378);
	}
	@SneakyThrows
	public static Jedis getjedis() {

		if (null != jedisPool) {
			Jedis resource = jedisPool.getResource();
			return resource;
		}
		throw new Exception("Jedispool is not work");
	}
}
