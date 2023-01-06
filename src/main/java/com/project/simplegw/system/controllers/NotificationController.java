package com.project.simplegw.system.controllers;

import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    @GetMapping("/count")
    public ResponseEntity<Object> getMyUncheckedNotificationsCount(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(notificationService.getUncheckedNotificationsCount(loginUser));
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getNotifications(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(notificationService.getNotifications(loginUser));
    }
}
