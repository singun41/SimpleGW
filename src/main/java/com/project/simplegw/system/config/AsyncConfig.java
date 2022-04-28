package com.project.simplegw.system.config;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public Executor getAsyncExecutor() {
        logger.info("커스텀 클래스 AsyncConfig의 getAsyncExecutor 메서드를 호출합니다. 'Executor' 인스턴스를 생성하여 리턴합니다.");

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
