package com.project.simplegw.schedule.services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.project.simplegw.system.vos.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleCountCacheService {
    public ScheduleCountCacheService() {
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = {Constants.CACHE_SCHEDULE_TODAY_SUMMARY, Constants.CACHE_SCHEDULE_TODAY_LIST}, allEntries = true)
    public void clear() {
        log.info("CacheEvict method clearCache() called. personal schedule 'summary' and 'list' both removed.");
    }
}
