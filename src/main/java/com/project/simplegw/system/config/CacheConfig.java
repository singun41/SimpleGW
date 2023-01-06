package com.project.simplegw.system.config;

import java.util.Arrays;

import com.project.simplegw.system.vos.Constants;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CacheConfig {
    public CacheConfig() {
        log.info("Configuration '" + this.getClass().getName() + "' has been created.");
    }

    @Bean(name = Constants.CACHE_MANAGER)
    CacheManager cacheManager() {
        log.info("Bean '{}' has been created.", Constants.CACHE_MANAGER);

        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();

        cacheManager.setAllowNullValues(true);
        cacheManager.setCacheNames(
            Arrays.asList(
                Constants.CACHE_BASECODE, Constants.CACHE_JOB_TITLES, Constants.CACHE_DAYOFF_CODES,
                
                Constants.CACHE_USER_PROFILES, Constants.CACHE_IMG_USER_PORTRAIT, Constants.CACHE_USER_ENVIRONMENT_SETTING,

                Constants.CACHE_NOTICE, Constants.CACHE_FREEBOARD, Constants.CACHE_POST_IT, Constants.CACHE_TEMPDOCS_COUNT,
                Constants.CACHE_HOLIDAYS, Constants.CACHE_ALARMS, Constants.CACHE_EDITOR_FORMS,

                Constants.CACHE_APPROVAL_PROCEED_COUNT, Constants.CACHE_APPROVAL_APPROVER_COUNT, Constants.CACHE_APPROVAL_REFERRER_COUNT,

                Constants.CACHE_SCHEDULE_COLOR_LIST, Constants.CACHE_SCHEDULE_CODE_PERSONAL, Constants.CACHE_SCHEDULE_TODAY_SUMMARY, Constants.CACHE_SCHEDULE_TODAY_LIST
            )
        );

        return cacheManager;
    }
}
