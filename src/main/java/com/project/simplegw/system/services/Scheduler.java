package com.project.simplegw.system.services;

import com.project.simplegw.common.services.AlarmService;
import com.project.simplegw.document.services.AttachmentsService;
import com.project.simplegw.document.services.DocsService;
import com.project.simplegw.member.services.MemberService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {
    /*
        스케줄링이 필요한 기능은 이 클래스에 등록해서 사용.
    */

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MemberService memberService;
    private final DocsService docsService;
    private final AttachmentsService attachmentsService;
    private final LoggingAop loggingAop;
    private final AlarmService alarmService;
    private final CacheService cacheService;

    private final static String EVERY_DAY_00H_00M_05S = "5 0 0 * * *";
    private final static String EVERY_30_SECONDS = "0/30 * * * * *";
    private final static String EVERY_30_MINUTES = "0 0/30 * * * *";

    @Autowired
    public Scheduler(
        MemberService memberService, DocsService docsService, AttachmentsService attachmentsService,
        LoggingAop loggingAop, AlarmService alarmService, CacheService cacheService
    ) {
        this.memberService = memberService;
        this.docsService = docsService;
        this.attachmentsService = attachmentsService;
        this.loggingAop = loggingAop;
        this.alarmService = alarmService;
        this.cacheService = cacheService;

        logger.info("스케줄러 서비스를 시작하였습니다.");
    }

    @Scheduled(cron = EVERY_DAY_00H_00M_05S)
    private void retireMemberToDisabled() {
        memberService.retireMemberToDisabled();
    }

    @Scheduled(cron = EVERY_DAY_00H_00M_05S)
    private void setNoticeFixedList() {
        docsService.setNoticeFixedList();
    }

    @Scheduled(cron = EVERY_DAY_00H_00M_05S)
    private void setAttachmentsDirectories() {
        attachmentsService.setDailyPath();
    }

    @Scheduled(cron = EVERY_30_SECONDS)
    private void writingLogForAop() {
        try {
            loggingAop.writingFileFromQueue();
        } catch(Exception e) {
            logger.warn("{}{}aop logging 스케줄러 동작 중 에러가 발생하였습니다.", e.getMessage(), System.lineSeparator());
        }
    }

    @Scheduled(cron = EVERY_DAY_00H_00M_05S)
    private void clearAlarms() {
        alarmService.clearAlarms();
    }

    @Scheduled(cron = EVERY_DAY_00H_00M_05S)
    private void clearCache() {
        cacheService.clearAll();
    }

    @Scheduled(cron = EVERY_30_MINUTES)
    private void printCache() {
        cacheService.printCache();
    }
}
