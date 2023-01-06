package com.project.simplegw.member.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.member.dtos.admin.receive.DtorMemberAddOnUpdate;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberCreate;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberUpdate;
import com.project.simplegw.member.dtos.admin.receive.DtorPwForceUpdate;
import com.project.simplegw.member.services.MemberAdminService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
public class MemberAdminController {
    private final MemberAdminService service;

    @Autowired
    public MemberAdminController(MemberAdminService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    
    @GetMapping(path = "/all", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> getMembers(@RequestParam boolean isResigned, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        return ResponseConverter.ok( service.getMembers(isResigned) );
    }
    

    @PostMapping
    public ResponseEntity<Object> createMember(@Validated @RequestBody DtorMemberCreate dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        if(result.hasErrors())
            return ResponseConverter.badRequest(result);

        return ResponseConverter.message(
            service.create(dto, loginUser), ResponseMsg.INSERTED
        );
    }


    @PatchMapping("/{memberId}")
    public ResponseEntity<Object> updateMember(@PathVariable Long memberId, @Validated @RequestBody DtorMemberUpdate dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        if(result.hasErrors())
            return ResponseConverter.badRequest(result);

        return ResponseConverter.message(
            service.update(memberId, dto, loginUser), ResponseMsg.UPDATED
        );
    }


    @PatchMapping("/{memberId}/pw")
    public ResponseEntity<Object> updateMemberPw(@PathVariable Long memberId, @Validated @RequestBody DtorPwForceUpdate dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        if(result.hasErrors())
            return ResponseConverter.badRequest(result);

        return ResponseConverter.message(
            service.updateMemberPw(memberId, dto, loginUser), ResponseMsg.UPDATED
        );
    }



    @PatchMapping("/{memberId}/dayoff-count")
    public ResponseEntity<Object> updateMemberDayoffCount(@PathVariable Long memberId, @Validated @RequestBody DtorMemberAddOnUpdate dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        if(result.hasErrors())
            return ResponseConverter.badRequest(result);

        return ResponseConverter.message(
            service.updateMemberDayoffCount(memberId, dto), ResponseMsg.UPDATED
        );
    }
}
