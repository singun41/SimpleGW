package com.project.simplegw.work.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.MenuAuthorityService;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.work.dtos.receive.DtorWorkRecord;
import com.project.simplegw.work.services.WorkRecordService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/work-record")
public class WorkRecordController {
    private final WorkRecordService service;
    private final MenuAuthorityService authority;

    @Autowired
    public WorkRecordController(WorkRecordService service, MenuAuthorityService authority) {
        this.service = service;
        this.authority = authority;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @GetMapping("/personal/{year}/{month}/{day}")
    public ResponseEntity<Object> getMyWorkRecord(@PathVariable int year, @PathVariable int month, @PathVariable int day, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isReadable(Menu.WORK_RECORD, loginUser, loginUser.getMember().getId()) )
            return ResponseConverter.unauthorized();

        LocalDate workDate = LocalDate.of(year, month, day);
        return ResponseConverter.ok( service.getMyWorkRecord(workDate, loginUser) );
    }


    @PatchMapping("/personal")
    public ResponseEntity<Object> save(@Validated @RequestBody DtorWorkRecord dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isWritable(Menu.WORK_RECORD, loginUser) )
            return ResponseConverter.unauthorized();

        if(result.hasErrors())
            return ResponseConverter.badRequest(result);

        return ResponseConverter.message( service.save(dto, loginUser), ResponseMsg.INSERTED );
    }


    @GetMapping("/team/{year}/{month}/{day}")
    public ResponseEntity<Object> getMyTeamWorkRecordList(@PathVariable int year, @PathVariable int month, @PathVariable int day, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isReadable(Menu.WORK_RECORD_TEAM, loginUser, loginUser.getMember().getId()) )
            return ResponseConverter.unauthorized();
        
        LocalDate workDate = LocalDate.of(year, month, day);
        return ResponseConverter.ok( service.getMyTeamWorkRecordList(workDate, loginUser) );
    }


    @GetMapping("/{team}/{year}/{month}/{day}")
    public ResponseEntity<Object> getWorkRecordList(@PathVariable String team, @PathVariable int year, @PathVariable int month, @PathVariable int day, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isReadable(Menu.WORK_RECORD_LIST, loginUser, loginUser.getMember().getId()) )
            return ResponseConverter.unauthorized();

        LocalDate workDate = LocalDate.of(year, month, day);
        return ResponseConverter.ok( service.getWorkRecordList(workDate, team) );
    }
}
