package com.project.simplegw.common.services;

import java.util.List;

import com.project.simplegw.system.security.SecurityUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlarmController {
    private final AlarmService alarmService;

    @Autowired
    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @GetMapping(path = "/alarm/count/not-checked")
    public long getAlarmNotCheckedCount(@AuthenticationPrincipal SecurityUser loginUser) {
        return alarmService.getAlarmNotCheckedCount(loginUser.getMember().getId());
    }

    @GetMapping(path = "/alarm/list")
    public List<String> getAlarmContent(@AuthenticationPrincipal SecurityUser loginUser) {
        alarmService.updateAlarmChecked(loginUser.getMember().getId());
        return alarmService.getAlarmContent(loginUser.getMember().getId());
    }
}
