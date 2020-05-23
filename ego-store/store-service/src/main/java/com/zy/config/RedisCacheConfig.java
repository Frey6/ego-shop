package com.zy.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisCacheConfig {

  private CacheProperties cacheProperties;

  /**
   * 该配置类被构造时，它里面的参数，将有Spring 的ioc 容器提供
   * @param cacheProperties
   */
  public RedisCacheConfig(CacheProperties cacheProperties) {
    this.cacheProperties = cacheProperties;
  }

  /**
   * 给ioc 容器里面注入对象
   *
   * @return
   */
  @Bean
  public RedisCacheConfiguration redisCacheConfiguration() {
    CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
    org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
      .defaultCacheConfig();

    // 定义Redis缓存的序列化的形式
    // 在这个RedisSerializer类里面提交了3 种常用的序列化形式
    /**
     * 1 java  jdk的序列化 (默认的形式)
     * 2 json  jackson （我们可以手动切换）
     * 3 String String的序列化(主要用在字符串的存储，不是对象) 对key 一般使用的是String的序列化
     */
    config = config.serializeValuesWith(
      RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
    // 缓存的过期时间，一般我们在配置文件里面设置
    if (redisProperties.getTimeToLive() != null) {
      config = config.entryTtl(redisProperties.getTimeToLive());
    }

    // 是否给缓存添加前缀
    if (redisProperties.getKeyPrefix() != null) {
      config = config.prefixKeysWith(redisProperties.getKeyPrefix());
    }

    // 空值的缓存
    if (!redisProperties.isCacheNullValues()) {
      config = config.disableCachingNullValues();
    }
    /**
     * 是否禁用key
     */
    if (!redisProperties.isUseKeyPrefix()) {
      config = config.disableKeyPrefix();
    }
    return config;
  }
}
