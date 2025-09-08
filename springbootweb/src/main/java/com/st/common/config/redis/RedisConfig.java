package com.st.common.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.util.concurrent.TimeUnit;

/**
 * 概述: 提供 StringRedisTemplate 与通用 RedisTemplate 的序列化配置, 以及一个示例服务方法
 * 功能清单:
 * 1) 统一 Key 使用字符串序列化避免可读性问题
 * 2) Value 使用 JacksonJson 序列化, 兼容对象读写
 * 3) 提供 setAndGet 示例方法, 演示写入并读取
 * 使用示例:
 * RedisOps ops = new RedisOps(stringRedisTemplate);
 * String got = ops.setAndGet("k", "v", 60);
 * 注意事项:
 * 1) 确保 application.yml 已正确配置 spring.redis.* 且 Redis 可达
 * 2) 若未启用 ACL, 删除 username 配置
 * 入参与出参:
 * setAndGet(String key, String value, long ttlSeconds) -> 返回读取到的值
 * 可能异常:
 * 1) org.springframework.data.redis.RedisConnectionFailureException 连接失败
 * 2) 序列化异常: 配置不当时读写对象可能失败
 */
@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        org.springframework.data.redis.core.RedisTemplate<String, Object> template = new org.springframework.data.redis.core.RedisTemplate<>();
        template.setConnectionFactory(factory);
        StringRedisSerializer keySer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> valSer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        valSer.setObjectMapper(om);
        template.setKeySerializer(keySer);
        template.setValueSerializer(valSer);
        template.setHashKeySerializer(keySer);
        template.setHashValueSerializer(valSer);
        template.afterPropertiesSet();
        return template;
    }

    public static class RedisOps {
        private final StringRedisTemplate srt;
        public RedisOps(StringRedisTemplate srt) { this.srt = srt; }

        /**
         * 概述: 写入一个键值并设置过期时间, 随后读取返回
         * 功能清单:
         * 1) set key value ex ttlSeconds
         * 2) get key
         * 使用示例:
         * new RedisOps(srt).setAndGet("demo", "ok", 30);
         * 注意事项:
         * 1) ttlSeconds <= 0 将不设置过期
         * 入参与出参:
         * key 键
         * value 值
         * ttlSeconds 过期秒数
         * 返回 读取回来的值
         * 可能异常:
         * 连接失败抛出运行时异常
         */
        public String setAndGet(String key, String value, long ttlSeconds) {
            if (ttlSeconds > 0) {
                srt.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            } else {
                srt.opsForValue().set(key, value);
            }
            return srt.opsForValue().get(key);
        }
    }
}

