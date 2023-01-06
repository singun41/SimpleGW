package com.project.simplegw.system.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.project.simplegw.document.entities.Docs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardSharingNotificationService {
    private final NotificationService notificationService;
    private final SseNotificationService sseNotificationService;

    @Autowired
    public BoardSharingNotificationService(NotificationService notificationService, SseNotificationService sseNotificationService) {
        this.notificationService = notificationService;
        this.sseNotificationService = sseNotificationService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    @Async
    public void create(Docs docs, List<Long> referrers) {
        String content = new StringBuilder(docs.getType().getTitle()).append(" '").append(docs.getTitle()).append("' 문서를 공유 받았습니다.").toString();
        referrers.forEach(id -> {
            // 공유받은 멤버들의 시스템 알림 생성.
            notificationService.create(id, content);
            sseNotificationService.sendNotification(id);
        });
    }
}
