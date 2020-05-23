package com.zy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.net.UnknownHostException;

@Configuration
public class RedisTemplateConfig {


  /**
   * 我们需要设置一个RedisTemplate的序列化形式
   * @param redisConnectionFactory
   * @return
   * @throws UnknownHostException
   */
  @Bean
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
    throws UnknownHostException {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    // 对于普通的k-v 的序列化
    template.setKeySerializer(RedisSerializer.string());
    template.setValueSerializer(RedisSerializer.json());

    // 对于存储hash 的序列化
    template.setHashKeySerializer(RedisSerializer.string());
    template.setHashValueSerializer(RedisSerializer.json());
    return template;
  }
}
