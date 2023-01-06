package com.project.simplegw.document.approval.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsMin;
import com.project.simplegw.document.approval.dtos.send.DtosReferrer;
import com.project.simplegw.document.approval.entities.Referrer;
import com.project.simplegw.document.approval.helpers.ApprovalConverter;
import com.project.simplegw.document.approval.helpers.DtosApprovalDocsMinConverter;
import com.project.simplegw.document.approval.repositories.ReferrerRepo;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.BoardSharingNotificationService;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ReferrerService {
    private final ReferrerRepo repo;
    private final MemberService memberService;
    private final ApprovalCountService countService;
    private final ApprovalConverter converter;
    private final BoardSharingNotificationService boardSharingNotiService;

    @Autowired
    public ReferrerService(
        ReferrerRepo repo, MemberService memberService, ApprovalCountService countService, ApprovalConverter converter,
        BoardSharingNotificationService boardSharingNotiService
    ) {
        this.repo = repo;
        this.memberService = memberService;
        this.countService = countService;
        this.converter = converter;
        this.boardSharingNotiService = boardSharingNotiService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서의 참조자 등록 및 수정 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    ServiceMsg create(Docs docs, Long[] arrReferrerId, LoginUser loginUser) {
        if(arrReferrerId == null || arrReferrerId.length == 0)   // 참조자는 없는 경우도 있으므로
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        try {
            List<Referrer> referrers = new ArrayList<>();

            Arrays.stream(arrReferrerId)
                .filter(e -> ! e.equals( loginUser.getMember().getId() ))   // 유저가 본인을 등록한 경우 제외한다.
                .distinct().forEach(e -> {
                    Referrer referrer = Referrer.builder().docs(docs).build().setReferrer( memberService.getMemberData(e) );
                    referrers.add(referrer);
                });

            repo.saveAll(referrers);
            referrers.forEach(e -> countService.removeReferrerDocsCntCache(e.getMemberId(), true));   // 결재 참조받은 모든 멤버들의 참조 카운트 캐시 업데이트

            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("save Exception.");
            log.warn("parameters: {}, {}", docs.toString(), arrReferrerId.toString());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재 참조자 저장 에러입니다. 관리자에게 문의하세요.");
        }
    }

    ServiceMsg update(Docs docs, Long[] arrReferrerId, LoginUser loginUser) {
        try {
            List<Referrer> referrers = repo.findByDocsIdOrderById(docs.getId());
            if(referrers != null)
                delete(docs, referrers);
            
            return create(docs, arrReferrerId, loginUser);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("update Exception.");
            log.warn("parameters: {}, {}, user: {}", docs.toString(), arrReferrerId.toString(), loginUser.getMember().getId());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재 참조자 수정 에러입니다. 관리자에게 문의하세요.");
        }
    }

    private void delete(Docs docs, List<Referrer> referrers) throws Exception {
        repo.deleteAllInBatch(referrers);

        referrers.stream().mapToLong(Referrer::getMemberId).forEach(e -> countService.removeReferrerDocsCntCache(e, true));   // 결재 참조받은 모든 멤버들의 참조 카운트 캐시 제거
    }


    public ServiceMsg add(Docs docs, Long[] arrReferrerId, LoginUser loginUser) {   
        try {
            List<Long> list = Arrays.asList(arrReferrerId);
            List<Long> savedIds = repo.findByDocsIdOrderById(docs.getId()).stream().mapToLong(Referrer::getMemberId).boxed().collect(Collectors.toList());
            
            list.removeAll(savedIds);   // 기존에 저장된 멤버가 중복이면 제거.
            list = list.stream().distinct().filter(e -> !e.equals(loginUser.getMember().getId())) .collect(Collectors.toList());   // 본인 및 중복 제거한 최종 리스트 만들기.

            repo.saveAll(
                list.stream().map(e -> Referrer.builder().docs(docs).build().setReferrer( memberService.getMemberData(e) )).collect(Collectors.toList())
            );

            switch(docs.getType().getGroup()) {
                case BOARD -> {
                    if(docs.getType() == DocsType.MINUTES)
                        boardSharingNotiService.create(docs, list);   // 회의록 공유 멤버들에게 시스템 알림 보내기.
                }
                case APPROVAL -> list.forEach(e -> countService.removeReferrerDocsCntCache(e, true));   // 추가한 멤버들의 결재참조 카운트 캐시 업데이트
            }

            return new ServiceMsg().setResult(ServiceResult.SUCCESS);
        
        } catch(Exception e) {
            e.printStackTrace();
            log.warn("addToDocs Exception.");
            log.warn("parameters: {}, {}, user: {}", docs.toString(), arrReferrerId.toString(), loginUser.getMember().getId());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("참조자 추가 에러입니다. 관리자에게 문의하세요.");
        }
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서의 참조자 등록 및 수정 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서 view page에서 필요한 참조자 정보 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public List<DtosReferrer> getReferrers(Docs docs, LoginUser loginUser) {   // 문서 공유기능이 필요한 회의록에서도 사용하기 위해 public으로 전환.
        List<Referrer> referrers = repo.findByDocsIdOrderById(docs.getId());
        updateChecked(referrers, loginUser);

        return referrers.stream().map( e -> converter.getDtosReferrer(e).setChecked(e.getCheckedDatetime() != null) ).collect(Collectors.toList());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서 view page에서 필요한 참조자 정보 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 참조자 확인 시간 업데이트 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    private void updateChecked(List<Referrer> referrers, LoginUser loginUser) {
        referrers.stream().filter(
            e -> e.getMemberId().equals(loginUser.getMember().getId()) && e.getCheckedDatetime() == null
        ).findFirst().ifPresent(e -> {
                countService.removeReferrerDocsCntCache(loginUser.getMember().getId(), false);
                repo.save(e.checked());
        });
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 참조자 확인 시간 업데이트 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- List search ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    List<Referrer> getUncheckedReferrers(LoginUser loginUser) {   // 확인하지 않은 문서 찾기
        return repo.findByMemberIdOrderById(loginUser.getMember().getId()).stream().filter(e -> e.getCheckedDatetime() == null).collect(Collectors.toList());
    }

    List<Referrer> getReferrers(LocalDate dateFrom, LocalDate dateTo, LoginUser loginUser) {
        return repo.findByMemberIdOrderById(loginUser.getMember().getId()).stream().collect(Collectors.toList());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- List search ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 참조자로 받은 결재문서의 기간 검색 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    List<DtosApprovalDocsMin> getDocsForReferrer(DocsType type, LocalDate dateFrom, LocalDate dateTo, LoginUser loginUser) {
        List<Object[]> objList = repo.findForReferrer(loginUser.getMember().getId(), type, dateFrom, dateTo);
        return objList.stream().map( e -> DtosApprovalDocsMinConverter.fromObjs(e) ).collect(Collectors.toList());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 참조자로 받은 결재문서의 기간 검색 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
