package com.project.simplegw.schedule.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.schedule.dtos.admin.receive.DtorColor;
import com.project.simplegw.schedule.services.ScheduleService;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.schedule.vos.SearchOption;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService service;

    @Autowired
    public ScheduleController(ScheduleService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/color-list/{type}")
    public ResponseEntity<Object> getColorList(@PathVariable String type, @AuthenticationPrincipal LoginUser loginUser) {
        if( loginUser.getMember().getRole() != Role.ADMIN)
            return ResponseConverter.unauthorized();
        
        return ResponseConverter.ok(
            service.getColorList( ScheduleType.valueOf(type.toUpperCase()) )
        );
    }


    @PatchMapping("/color-list/{type}")
    public ResponseEntity<Object> updateColor(
        @PathVariable String type, @Validated @RequestBody List<DtorColor> dtos, BindingResult result, @AuthenticationPrincipal LoginUser loginUser
    ) {
        if( loginUser.getMember().getRole() != Role.ADMIN)
            return ResponseConverter.unauthorized();
        
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.updateColor(ScheduleType.valueOf(type.toUpperCase()), dtos), ResponseMsg.UPDATED
        );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //



    @GetMapping(path = "/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> getScheduleList(
        @RequestParam String type, @RequestParam String option, @RequestParam int year, @RequestParam int month, @AuthenticationPrincipal LoginUser loginUser
    ) {
        return ResponseConverter.ok(
            service.getScheduleList(ScheduleType.valueOf(type.toUpperCase()), SearchOption.valueOf(option.toUpperCase()), year, month, loginUser)
        );
    }
}
