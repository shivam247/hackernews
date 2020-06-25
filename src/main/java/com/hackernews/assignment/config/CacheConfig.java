package com.hackernews.assignment.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.hackernews.assignment.exception.CustomCacheErrorHandler;

@Configuration
@EnableConfigurationProperties(CacheConfigurationProperties.class)
public class CacheConfig extends CachingConfigurerSupport{

	private static RedisCacheConfiguration createCacheConfiguration(long timeoutInSeconds) {
		return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(timeoutInSeconds));
	}

	@Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }
	 
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(cf);
		return redisTemplate;
	}

	@Bean
	public RedisCacheConfiguration cacheConfiguration(CacheConfigurationProperties properties) {
		return createCacheConfiguration(properties.getTimeoutSeconds());
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
			CacheConfigurationProperties properties) {
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

		for (Entry<String, Long> cacheNameAndTimeout : properties.getCacheExpirations().entrySet()) {
			cacheConfigurations.put(cacheNameAndTimeout.getKey(), createCacheConfiguration(cacheNameAndTimeout.getValue()));
		}

		return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(cacheConfiguration(properties))
				.withInitialCacheConfigurations(cacheConfigurations).build();
	}
}