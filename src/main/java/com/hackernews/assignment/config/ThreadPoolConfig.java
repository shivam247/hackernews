package com.hackernews.assignment.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {
	
	
	@Value("${threadpool.corePoolSize:20}")
	private int corePoolSize;
	
	@Value("${threadpool.maxPoolSize:30}")
	private int maxPoolSize;
	
	@Value("${threadpool.queueCapacity:10}")
	private int queueCapacity;
	
	@Value("${threadpool.keepAliveSec:60}")
	private int keepAliveSec;
	
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setKeepAliveSeconds(keepAliveSec);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor. CallerRunsPolicy());
        taskExecutor.setThreadNamePrefix("taskExecutor-thread");
        taskExecutor.initialize();
        return taskExecutor;

    }
}