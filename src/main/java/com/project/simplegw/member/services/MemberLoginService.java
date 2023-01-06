package com.project.simplegw.member.services;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.entities.Member;
import com.project.simplegw.system.security.LoginUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MemberLoginService {
    private final MemberService memberService;

    @Autowired
    public MemberLoginService(MemberService memberService) {
        this.memberService = memberService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    public Member getMember(String userId) {
        return memberService.getMember(userId);
    }

    public MemberData getMemberData(LoginUser loginUser) {
        return memberService.getMemberData(loginUser);
    }
}
