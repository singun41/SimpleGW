package com.project.simplegw.member.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.member.dtos.receive.DtorEnvSetting;
import com.project.simplegw.member.dtos.receive.DtorProfile;
import com.project.simplegw.member.dtos.receive.DtorPwChange;
import com.project.simplegw.member.services.MemberAddOnService;
import com.project.simplegw.member.services.MemberClientService;
import com.project.simplegw.member.services.MemberEnvSettingService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MemberClientController {
    private final MemberClientService service;
    private final MemberAddOnService addOnService;
    private final MemberEnvSettingService envSettingService;

    @Autowired
    public MemberClientController(MemberClientService service, MemberAddOnService addOnService, MemberEnvSettingService envSettingService) {
        this.service = service;
        this.addOnService = addOnService;
        this.envSettingService = envSettingService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    
    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( service.getProfile(loginUser) );
    }


    @GetMapping(path ="/old-pw-matched", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> isOldPasswordMatched(@RequestParam String oldPw, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( service.isOldPasswordMatched(oldPw, loginUser) );
    }


    @PatchMapping("/profile")
    public ResponseEntity<Object> updateProfile(@Validated @RequestBody DtorProfile dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.updateProfile(dto, loginUser), ResponseMsg.UPDATED
        );
    }


    @PatchMapping("/password")
    public ResponseEntity<Object> updateMyPassword(@Validated @RequestBody DtorPwChange dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.updateMyPassword(dto, loginUser), ResponseMsg.UPDATED
        );
    }



    @GetMapping("/environment-setting")
    public ResponseEntity<Object> getEnvironmentSetting(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( envSettingService.getEnvSetting(loginUser) );
    }


    @PatchMapping("/environment-setting")
    public ResponseEntity<Object> updateEnvSetting(@Validated @RequestBody DtorEnvSetting dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            envSettingService.update(dto, loginUser), ResponseMsg.UPDATED
        );
    }


    @GetMapping("/dayoff-count")
    public ResponseEntity<Object> getDayoffCount(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( addOnService.getDayoffCount(loginUser) );
    }
}
