package com.project.simplegw.schedule.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.schedule.dtos.receive.DtorScheduleIncludedTime;
import com.project.simplegw.schedule.services.PersonalScheduleService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/schedule/personal")
public class PersonalScheduleController {
    private final PersonalScheduleService service;

    @Autowired
    public PersonalScheduleController(PersonalScheduleService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    @PostMapping
    public ResponseEntity<Object> save(@Validated @RequestBody DtorScheduleIncludedTime dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message( service.save(dto, loginUser), ResponseMsg.INSERTED );
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Validated @RequestBody DtorScheduleIncludedTime dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message( service.update(id, dto, loginUser), ResponseMsg.UPDATED );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message( service.delete(id, loginUser), ResponseMsg.DELETED );
    }
}
