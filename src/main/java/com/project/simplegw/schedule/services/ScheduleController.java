package com.project.simplegw.schedule.services;

import java.util.List;

import com.project.simplegw.common.dtos.BasecodeDTO;
import com.project.simplegw.schedule.dtos.ScheduleDTO;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.system.security.SecurityUser;
import com.project.simplegw.system.services.ResponseEntityConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping(path = "/schedule/{type}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<ScheduleDTO> getScheduleList(@PathVariable ScheduleType type, @RequestParam int year, @RequestParam int month, @AuthenticationPrincipal SecurityUser loginUser) {
        return scheduleService.getScheduleList(type, year, month, loginUser.getMember().getId());
    }

    @PostMapping(path = "/schedule")
    public ResponseEntity<Object> saveSchedule(@RequestBody ScheduleDTO dto, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(scheduleService.saveSchedule(dto, loginUser.getMember()));
    }

    @GetMapping(path = "/schedule/code-list/{type}")
    public List<BasecodeDTO> getScheduleCodeListInType(@PathVariable ScheduleType type) {
        return scheduleService.getScheduleCodeListInType(type);
    }

    @DeleteMapping(path = "/schedule/{id}")
    public ResponseEntity<Object> deleteSchedule(@PathVariable Long id, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(scheduleService.deleteSchedule(id, loginUser.getMember()));
    }

    @GetMapping(path = "/schedule/company-event")
    public List<ScheduleDTO> getCompanyEvent() {
        return scheduleService.getCompanyEvent();
    }
}
