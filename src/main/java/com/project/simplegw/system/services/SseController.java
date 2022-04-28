package com.project.simplegw.system.services;


import com.project.simplegw.system.security.SecurityUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {

    private final SseService sseService;

    @Autowired
    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/sse/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal SecurityUser loginUser) {
        return sseService.subscribe(loginUser.getMember().getId());
    }

    @GetMapping("/sse/disconnect")
    public void deleteSseEmitter(@AuthenticationPrincipal SecurityUser loginUser) {
        sseService.deleteSseEmitter(loginUser.getMember().getId());
    }
}