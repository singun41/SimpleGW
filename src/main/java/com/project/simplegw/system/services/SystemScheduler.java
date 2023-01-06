package com.project.simplegw.system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.project.simplegw.member.services.MemberPortraitService;
import com.project.simplegw.schedule.services.ScheduleCountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemScheduler {

    // cron expression: s m h d M mon-fri y
    // mon-fri --> 0:sun ~ 5:fri, 6:sat, 7:sun (0, 7 둘 다 sunday)
    // year는 생략 가능
    // * --> all
    // ? --> 어떤값이든 관계없음, 날짜와 요일만 가능
    // - --> 범위
    // , --> 여러 값
    // L --> 마지막 값, 날짜와 요일만 가능
    // W --> 월~금 또는 가장 가까운 월, 금을 설정
    // # --> 몇번 째 특정 요일 지정
    
    // samples
    // 0 0/5 * * * --> 매 5, 10, 15분... 5분 단위로 실행
    // 0 0 13 * * * --> 매일 13시마다 실행
    
    private static final String FIRST_DAY_OF_EVERY_MONTHS = "5 0 0 1 1/1 *";
    
    private static final String EVERY_MONDAY_MIDNIGHTS_05 = "5 0 0 ? * MON";

    private static final String EVERY_MIDNIGHTS_05 = "5 0 0 * * *";   // 5초 정도 여유를 주기.

    // private static final String EVERY_HOURS = "0 0 0/1 * * *";
    private static final String EVERY_30_MINS = "0 0/30 * * * *";
    // private static final String EVERY_30_SECS = "0/30 * * * * *";
    // private static final String EVERY_15_SECS = "0/15 * * * * *";


    private final SseService sseService;
    private final CacheService cacheService;
    private final NotificationService notiService;
    private final ScheduleCountService scheduleCountService;
    private final MemberPortraitService portraitService;
    private final SystemService systemService;


    @Autowired
    public SystemScheduler(SseService sseService, CacheService cacheService, NotificationService notiService, ScheduleCountService scheduleCountService, MemberPortraitService portraitService, SystemService systemService) {
        this.sseService = sseService;
        this.cacheService = cacheService;
        this.notiService = notiService;
        this.scheduleCountService = scheduleCountService;
        this.portraitService = portraitService;
        this.systemService = systemService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    @Scheduled(cron = EVERY_30_MINS)
    private void sseMonitoring() {
        sseService.monitoring();
    }




    // @Scheduled(cron = EVERY_15_SECS)
    // private void cacheMonitoring() {
    //     cacheService.monitoring();
    // }



    
    @Scheduled(cron = EVERY_MIDNIGHTS_05)
    private void removeAlarms() {
        log.info("execute removeAlarms");
        cacheService.removeAlarms();
    }

    @Scheduled(cron = EVERY_MIDNIGHTS_05)
    private void removeOldNotifications() {
        log.info("execute removeOldNotifications");
        notiService.removeOldNotifications();
    }

    @Scheduled(cron = EVERY_MIDNIGHTS_05)
    private void removeSchedule() {
        log.info("execute removeScheduleTodaySummary");
        cacheService.removeScheduleCaches();
        scheduleCountService.removeOldScheduleCountEntities();
    }




    @Scheduled(cron = EVERY_MONDAY_MIDNIGHTS_05)
    private void diskUsageScan() {
        log.info("execute diskUsageScan");
        systemService.diskUsageScan();
    }





    @Scheduled(cron = FIRST_DAY_OF_EVERY_MONTHS)
    private void deleteResignedPortrait() {
        log.info("execute deleteResignedPortrait");
        portraitService.deleteResignedPortrait();
    }
}
