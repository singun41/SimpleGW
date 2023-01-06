package com.project.simplegw.member.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.document.approval.repositories.details.dayoff.DayoffRepo;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberAddOnUpdate;
import com.project.simplegw.member.dtos.admin.send.DtosMemberAddOn;
import com.project.simplegw.member.dtos.send.DtosDayoffCnt;
import com.project.simplegw.member.entities.MemberAddOn;
import com.project.simplegw.member.helpers.MemberAddOnConverter;
import com.project.simplegw.member.repositories.MemberAddOnRepo;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MemberAddOnService {
    private final MemberAddOnRepo repo;
    private final MemberAddOnConverter converter;
    private final DayoffRepo dayoffRepo;   // 순환참조 문제가 있어 service 대신 repository 를 직접 가져온다.
    
    @Autowired
    public MemberAddOnService(MemberAddOnRepo repo, MemberAddOnConverter converter, DayoffRepo dayoffRepo) {
        this.repo = repo;
        this.converter = converter;
        this.dayoffRepo = dayoffRepo;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    public DtosDayoffCnt getDayoffCount(LoginUser loginUser) {
        return converter.getDayoffCnt(repo.findByMemberId(loginUser.getMember().getId()).orElseGet(MemberAddOn::new));
    }


    @Async
    public void updateMemberDayoffCount(Docs docs) {   // ApproverService 에서 휴가신청서 최종 결재 처리시 호출.
        try {
            repo.findByMemberId(docs.getWriterId()).ifPresent(member -> member.updateDayoffUseCnt( dayoffRepo.findByDocsId(docs.getId()) ));
        } catch(Exception e) {
            e.printStackTrace();
            log.warn("updateMemberDayoffCount exception. called by 'ApproverSeervice'.");
            log.warn("paramters: {}", docs.toString());
        }
        
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    DtosMemberAddOn getDtosMemberAddOn(Long memberId) {
        return converter.getDtosmemberAddOn(repo.findByMemberId(memberId).orElseGet(MemberAddOn::new));
    }

    ServiceMsg updateMemberDayoffCount(Long memberId, DtorMemberAddOnUpdate dto) {
        try {
            repo.findByMemberId(memberId).ifPresent(member -> member.resetDayoffCnt(dto));
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("updateMemberDayoffCount exception.");
            log.warn("parameters: {}, user: {}", dto.toString(), memberId);
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("유저 연차 카운트 업데이트 오류입니다. 로그를 확인하세요.");
        }
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
