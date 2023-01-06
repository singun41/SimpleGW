package com.project.simplegw.member.services;

import com.project.simplegw.member.dtos.receive.DtorProfile;
import com.project.simplegw.member.dtos.receive.DtorPwChange;
import com.project.simplegw.member.dtos.send.DtosProfile;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ServiceMsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MemberClientService {
    private final MemberService memberService;

    @Autowired
    public MemberClientService(MemberService memberService) {
        this.memberService = memberService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    public boolean isOldPasswordMatched(String oldPw, LoginUser loginUser) {
        return memberService.isOldPasswordMatched(oldPw, loginUser);
    }

    public DtosProfile getProfile(LoginUser loginUser) {
        return memberService.getProfile(loginUser).calcDuration();   // MemberService 클래스에서 캐싱하므로 여기서 계산해서 리턴.
    }

    public ServiceMsg updateProfile(DtorProfile dto, LoginUser loginUser) {
        return memberService.updateProfile(dto, loginUser);
    }

    public ServiceMsg updateMyPassword(DtorPwChange dto, LoginUser loginUser) {
        return memberService.updateMyPassword(dto, loginUser);
    }
}
