package com.project.simplegw.member.controllers;

import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("team")
public class TeamController {
    private final MemberService memberService;

    @Autowired
    public TeamController(MemberService memberService) {
        this.memberService = memberService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/{teamName}")
    public ResponseEntity<Object> getTeamMembers(@PathVariable String teamName) {
        return ResponseConverter.ok( memberService.getTeamMembers(teamName) );
    }

    @GetMapping("/{teamName}/without-me")
    public ResponseEntity<Object> getTeamMembersWithoutMe(@PathVariable String teamName, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( memberService.getTeamMembersWithoutMe(teamName, loginUser) );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
