package com.project.simplegw.system.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {
    
    public AsyncConfig() {
        log.info("Configuration '" + this.getClass().getName() + "' has been created.");
    }

    @Override
    public Executor getAsyncExecutor() {
        log.info("Custom 'ThreadPoolTaskExecutor' has been created.");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        int poolSize = Runtime.getRuntime().availableProcessors();

        executor.setCorePoolSize(poolSize / 2);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("async-");
        executor.initialize();

        return executor;
    }
}
