package com.project.simplegw.system.services;

import com.project.simplegw.system.vos.CacheNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CacheManager cacheManager;

    @Autowired
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        logger.info("CacheService를 로드하였습니다.");
    }

    public void clearAll() {   // Scheduler에서 실행.
        cacheManager.getCacheNames().forEach(e -> {
            cacheManager.getCache(e).clear();

            logger.info("캐시를 클리어합니다. cacheNames: {}", e);
        });
    }

    public void printCache() {   // Scheduler에서 실행.
        cacheManager.getCacheNames().forEach(e -> {
            if(e.equals(CacheNames.CKEDITOR_FORMS)) {
                logger.info("cacheNames: {}", e);
            } else {
                logger.info("cacheNames: {}, cache data: {}", e, cacheManager.getCache(e).getNativeCache().toString());
            }
        });
    }
}
