package com.project.simplegw.alarm.controllers;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.alarm.dtos.receive.DtorAlarm;
import com.project.simplegw.alarm.services.AlarmService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/alarm")
public class AlarmController {
    private final AlarmService service;

    @Autowired
    public AlarmController(AlarmService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    @GetMapping
    public ResponseEntity<Object> getAlarms(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( service.getAlarms(loginUser) );
    }

    @GetMapping("/today")
    public ResponseEntity<Object> getTodayAlarms(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(
            service.getAlarms(loginUser).stream().filter(e -> LocalDate.now().equals( e.getAlarmDate() )).collect(Collectors.toList())
        );
    }


    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody DtorAlarm dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.create(dto, loginUser), ResponseMsg.INSERTED
        );
    }


    @PatchMapping("/{alarmId}")
    public ResponseEntity<Object> update(@PathVariable Long alarmId, @Validated @RequestBody DtorAlarm dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.update(alarmId, dto, loginUser), ResponseMsg.UPDATED
        );
    }


    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Object> delete(@PathVariable Long alarmId, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            service.delete(alarmId, loginUser), ResponseMsg.DELETED
        );
    }
}
