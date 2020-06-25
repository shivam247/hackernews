package com.hackernews.assignment.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class CustomCacheErrorHandler implements CacheErrorHandler {

	private static final Logger logger = LogManager.getLogger(CustomCacheErrorHandler.class);

	@Override
	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
		logger.warn(String.format("An error occured while getting from Cache : %s, Key : %s", cache.getName(), key), exception);
	}

	@Override
	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
		logger.warn(String.format("An error occured while putting into Cache : %s, Key : %s", cache.getName(), key), exception);
	}

	@Override
	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
		logger.warn(String.format("An error occured while deleting from Cache : %s, Key : %s", cache.getName(), key), exception);
	}

	@Override
	public void handleCacheClearError(RuntimeException exception, Cache cache) {
		logger.warn(String.format("An error occured while clearing Cache : %s", cache.getName()), exception);
	}
}