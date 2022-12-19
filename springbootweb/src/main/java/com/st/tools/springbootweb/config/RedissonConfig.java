package com.st.tools.springbootweb.config;

import io.lettuce.core.api.push.PushListener;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author: st
 * @date: 2022/12/19 12:58
 * @version: 1.0
 * @description:
 */
@Component
public class RedissonConfig {

	@Bean
	public Redisson redisson() {

		Config config = new Config();
		SingleServerConfig singleServerConfig = config.useSingleServer().setAddress("localhost:6379").setDatabase(0);

		return (Redisson) Redisson.create(config);

	}

}
