package com.project.simplegw.system.services;

import java.util.Map;

import com.project.simplegw.system.vos.SseDataType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SseNotificationService {
    private final SseService sseService;

    @Autowired
    public SseNotificationService(SseService sseService) {
        this.sseService = sseService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    
    public void sendNotification(Long memberId) {
        sseService.send(memberId, Map.of(SseDataType.NOTIFICATION.name(), "1"));
    }
}
