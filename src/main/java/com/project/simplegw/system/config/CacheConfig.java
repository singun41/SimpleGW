package com.project.simplegw.system.config;

import java.util.List;

import com.project.simplegw.system.vos.CacheNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String CUSTOM_CACHE_MANAGER = "customCacheManager";

    @Bean(name = CUSTOM_CACHE_MANAGER)
    public CacheManager cacheManager() {
        logger.info("커스텀 캐시 매니저 '{}' 를 생성합니다.", CUSTOM_CACHE_MANAGER);

        ConcurrentMapCacheManager customCacheManager = new ConcurrentMapCacheManager();
    
        customCacheManager.setAllowNullValues(false);
        customCacheManager.setCacheNames(List.of(CacheNames.BOARD, CacheNames.APPROVAL_DOCS_KINDS, CacheNames.APPROVER_ROLES, CacheNames.CKEDITOR_FORMS));

        return customCacheManager;
    }
}
