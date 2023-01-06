package com.project.simplegw.system.services;

import com.project.simplegw.document.entities.Docs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommentNotificationService {
    private final NotificationService notificationService;
    private final SseNotificationService sseNotificationService;

    @Autowired
    public CommentNotificationService(NotificationService notificationService, SseNotificationService sseNotificationService) {
        this.notificationService = notificationService;
        this.sseNotificationService = sseNotificationService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- called from CommentService ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @Async
    public void create(Docs docs) {
        String content =
            new StringBuilder("새 댓글이 달렸습니다. ")
            .append("(")
            .append(docs.getType().getTitle()).append(", ").append(docs.getTitle())
            .append(")").toString();
        
        notificationService.create(docs.getWriterId(), content);   // 문서 작성자에게 알려줄 알림 내용 생성.
        sseNotificationService.sendNotification(docs.getWriterId());   // 문서 작성자에게 event 전달.
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- called from CommentService ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
