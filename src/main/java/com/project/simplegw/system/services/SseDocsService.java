package com.project.simplegw.system.services;

import com.project.simplegw.system.vos.SseDataType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SseDocsService {
    private final SseService sseService;

    @Autowired
    public SseDocsService(SseService sseService) {
        this.sseService = sseService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @Async
    public void sendNotice() {   // called from NoticeService
        sseService.sendToAll(SseDataType.NOTICE);
    }

    
    @Async
    public void sendFreeboard() {   // called from FreeboardService
        sseService.sendToAll(SseDataType.FREEBOARD);
    }
}
