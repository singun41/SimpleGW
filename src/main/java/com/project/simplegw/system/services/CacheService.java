package com.project.simplegw.system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.project.simplegw.system.vos.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CacheService {
    private final CacheManager cacheManager;
    
    @Autowired
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    /*
        정기적인 캐시 정리를 스케줄로 처리하기 위한 클래스.

        수동 캐시 삭제는 캐시를 설정하는 각각의 서비스에서 처리.
    */

    
    
    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Scheduling ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    // void monitoring() {   // 개발 단계에서 캐시 동작을 점검하기 위한 메서드
    //     cacheManager.getCacheNames().forEach(e -> {
    //         if(
    //             e.equals(Constants.CACHE_APPROVAL_APPROVER_COUNT) ||
    //             e.equals(Constants.CACHE_APPROVAL_REFERRER_COUNT) ||
    //             e.equals(Constants.CACHE_APPROVAL_PROCEED_COUNT)
    //         )
    //         log.info("Cache name: {}, saved object: {}", e, cacheManager.getCache(e).getNativeCache().toString());
    //     });
    // }
        
    void removeAlarms() {
        cacheManager.getCache(Constants.CACHE_ALARMS).clear();
        log.info("Cache '{}' all entries removed.", Constants.CACHE_ALARMS);
    }

    void removeScheduleCaches() {
        cacheManager.getCache(Constants.CACHE_SCHEDULE_TODAY_SUMMARY).clear();
        cacheManager.getCache(Constants.CACHE_SCHEDULE_TODAY_LIST).clear();
        log.info("All schedule caches removed.");
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Scheduling ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
