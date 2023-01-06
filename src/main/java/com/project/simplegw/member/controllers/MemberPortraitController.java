package com.project.simplegw.member.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.project.simplegw.member.services.MemberPortraitService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/portrait")
public class MemberPortraitController {
    private final MemberPortraitService service;

    @Autowired
    public MemberPortraitController(MemberPortraitService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    @GetMapping
    public ResponseEntity<byte[]> getPortrait(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(service.getPortrait(loginUser));
    }

    
    @PostMapping
    public ResponseEntity<Object> uploadPortrait(MultipartHttpServletRequest req, MultipartFile imgFile, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            service.uploadPortrait(req, loginUser), ResponseMsg.INSERTED
        );
    }


    @PatchMapping("/{memberId}")
    public ResponseEntity<Object> userPortraitCacheRefresh(@PathVariable Long memberId, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        service.userPortraitCacheRefresh(memberId);
        return ResponseConverter.ok(ResponseMsg.UPDATED);
    }
}
