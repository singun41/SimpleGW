package com.project.simplegw.member.services;

import java.util.List;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberAddOnUpdate;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberCreate;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberUpdate;
import com.project.simplegw.member.dtos.admin.receive.DtorPwForceUpdate;
import com.project.simplegw.member.dtos.admin.send.DtosMember;
import com.project.simplegw.member.dtos.admin.send.DtosMemberAddOn;
import com.project.simplegw.member.dtos.admin.send.DtosMemberDetails;
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
public class MemberAdminService {
    private final MemberService memberService;
    private final MemberAddOnService addonService;

    @Autowired
    public MemberAdminService(MemberService memberService, MemberAddOnService addonService) {
        this.memberService = memberService;
        this.addonService = addonService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    public List<DtosMember> getMembers(boolean isResigned) {
        return memberService.getMembers(isResigned);
    }

    public DtosMemberDetails getMemberDetails(Long memberId) {
        return memberService.getMemberDetails(memberId);
    }

    public DtosMember getMember(Long memberId) {
        return memberService.getDtosMember(memberId);
    }

    public ServiceMsg create(DtorMemberCreate dto, LoginUser loginUser) {
        return memberService.create(dto, loginUser);
    }

    public ServiceMsg update(Long memberId, DtorMemberUpdate dto, LoginUser loginUser) {
        return memberService.update(memberId, dto, loginUser);
    }

    public ServiceMsg updateMemberPw(Long memberId, DtorPwForceUpdate dto, LoginUser loginUser) {
        return memberService.updateMemberPw(memberId, dto, loginUser);
    }



    public DtosMemberAddOn getMemberAddOn(Long memberId) {
        MemberData member = memberService.getMemberData(memberId);
        return addonService.getDtosMemberAddOn(memberId).setId(memberId).setTeam(member.getTeam()).setJobTitle(member.getJobTitle()).setName(member.getName());
    }

    public ServiceMsg updateMemberDayoffCount(Long memberId, DtorMemberAddOnUpdate dto) {
        return addonService.updateMemberDayoffCount(memberId, dto);
    }
}
