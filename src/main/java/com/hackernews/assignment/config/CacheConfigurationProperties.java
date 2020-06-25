package com.hackernews.assignment.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cache")
public class CacheConfigurationProperties {

    private long timeoutSeconds = 60;
    private int redisPort = 6379;
	private String redisHost = "localhost";
    private Map<String, Long> cacheExpirations = new HashMap<>();
    
	public long getTimeoutSeconds() {
		return timeoutSeconds;
	}
	public void setTimeoutSeconds(long timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}
	public int getRedisPort() {
		return redisPort;
	}
	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}
	public String getRedisHost() {
		return redisHost;
	}
	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}
	public Map<String, Long> getCacheExpirations() {
		return cacheExpirations;
	}
	public void setCacheExpirations(Map<String, Long> cacheExpirations) {
		this.cacheExpirations = cacheExpirations;
	}
    
    
}