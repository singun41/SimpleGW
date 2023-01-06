package com.project.simplegw.document.approval.services;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsMin;
import com.project.simplegw.document.approval.dtos.send.DtosApprover;
import com.project.simplegw.document.approval.entities.Approver;
import com.project.simplegw.document.approval.helpers.ApprovalConverter;
import com.project.simplegw.document.approval.helpers.DtosApprovalDocsMinConverter;
import com.project.simplegw.document.approval.repositories.ApproverRepo;
import com.project.simplegw.document.approval.vos.Sign;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.schedule.services.PersonalScheduleService;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.MenuAuthorityService;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;
import com.project.simplegw.system.vos.SseApprovalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ApproverService {
    private final ApproverRepo repo;
    private final MemberService memberService;
    private final OngoingApprovalService ongoingApprovalService;
    private final ApprovalStatusService statusService;
    private final ApprovalCountService countService;
    private final ApprovalConverter converter;

    private final MenuAuthorityService authorityService;

    // 근태 관련 결재문서 완료시 스케줄 연동을 위해 추가.
    private final PersonalScheduleService scheduleService;
    

    @Autowired
    public ApproverService(
        ApproverRepo repo, MemberService memberService, OngoingApprovalService ongoingApprovalService,
        ApprovalStatusService statusService, ApprovalCountService countService, ApprovalConverter converter,
        MenuAuthorityService authorityService, PersonalScheduleService scheduleService
    ) {
        this.repo = repo;
        this.memberService = memberService;
        this.ongoingApprovalService = ongoingApprovalService;
        this.statusService = statusService;
        this.countService = countService;
        this.converter = converter;
        this.authorityService = authorityService;
        this.scheduleService = scheduleService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서의 결재자 등록 및 수정 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    ServiceMsg create(Docs docs, Long[] arrApproverId, LoginUser loginUser) {
        try {
            List<Approver> approvers = new ArrayList<>();

            List<Long> approverIds = Arrays.asList(arrApproverId);
            approverIds.stream()
                .filter(e -> ! e.equals( loginUser.getMember().getId() ))   // 유저가 본인을 등록한 경우 제외한다.
                .forEach(e-> {
                    approvers.add(
                        Approver.builder().docs(docs).seq(approverIds.indexOf(e)).sign(Sign.PROCEED).build().setApprover( memberService.getMemberData(e) )
                    );
                });

            List<DtosApprover> DtoApprovers = approvers.stream().map(converter::getDtosApprover).collect(Collectors.toList());

            repo.saveAll(approvers);                             // 1. 결재라인 저장
            ongoingApprovalService.create(docs, DtoApprovers);   // 2. 현재 결재자 정보 등록
            statusService.create(docs, DtoApprovers);            // 3. 결재문서의 결재문서 상태 등록
            
            countService.removeApproverDocsCntCache(arrApproverId[0], true);   // 첫번째 결재자의 결재 요청 카운트 캐시 업데이트

            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("create Exception.");
            log.warn("parameters: {}, {}, user: {}", docs.toString(), arrApproverId.toString(), loginUser.getMember().getId());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재자 저장 에러입니다. 관리자에게 문의하세요.");
        }
    }

    ServiceMsg update(Docs docs, Long[] arrApproverId, LoginUser loginUser) {   // update: delete and save.
        try {
            List<Approver> approvers = repo.findByDocsIdOrderBySeq(docs.getId());
            if(approvers != null)
                delete(docs, approvers);
            
            return create(docs, arrApproverId, loginUser);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("update Exception.");
            log.warn("parameters: {}, {}, user: {}", docs.toString(), arrApproverId.toString(), loginUser.getMember().getId());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재자 수정 에러입니다. 관리자에게 문의하세요.");
        }
    }

    private void delete(Docs docs, List<Approver> approvers) throws Exception {
        repo.deleteAllInBatch(approvers);
        ongoingApprovalService.delete(docs);
        statusService.delete(docs);

        countService.removeApproverDocsCntCache(approvers.get(0).getMemberId(), true);   // 첫번째 결재자의 결재 요청 카운트 캐시 제거
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서의 결재자 등록 및 수정 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재자 승인/반려 처리 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    ServiceMsg confirm(Docs docs, LoginUser loginUser) {
        Long currentUserId = loginUser.getMember().getId();

        try {
            List<Approver> approvers = repo.findByDocsIdOrderBySeq(docs.getId());
            Optional<Approver> target = approvers.stream().filter( e -> e.getMemberId().equals(currentUserId) ).findFirst();
        
            if(target.isPresent()) {
                Approver approver = target.get();
                repo.save( approver.confirmed() );                   // 승인

                List<DtosApprover> DtoApprovers = approvers.stream().map(converter::getDtosApprover).collect(Collectors.toList());

                statusService.update(docs, DtoApprovers);            // 결재문서의 결재 상태 업데이트
                ongoingApprovalService.update(docs, DtoApprovers);   // 현재 결재자 정보 업데이트


                if(approvers.lastIndexOf(approver) == (approvers.size() - 1)) {
                    countService.removeProceedDocsCntCache(SseApprovalType.CONFIRMED, docs);   // 마지막 결재자가 승인을 했다면 최종 승인이므로 결재문서 등록자에게 승인 알림.
                    scheduleService.relatedApprovalDocs(docs);   // 근태 관련 문서인 경우 개인 일정에 자동 등록.
                    
                } else {
                    countService.removeApproverDocsCntCache(approvers.get( approvers.indexOf(approver) + 1 ).getMemberId(), true);   // 다음 결재자에게 알림 전송.
                }

                countService.removeApproverDocsCntCache( currentUserId, false );   // 승인 처리한 결재자의 카운트 초기화, 알림을 전송하지 않음.
                return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
            } else {
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재자 정보가 없습니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("confirmed Exception.");
            log.warn("parameters: {}, user: {}", docs.toString(), currentUserId);
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재승인 에러입니다. 관리자에게 문의하세요");
        }
    }


    ServiceMsg reject(Docs docs, LoginUser loginUser) {
        Long currentUserId = loginUser.getMember().getId();

        try {
            List<Approver> approvers = repo.findByDocsIdOrderBySeq(docs.getId());
            Optional<Approver> target = approvers.stream().filter( e -> e.getMemberId().equals(currentUserId) ).findFirst();
        
            if(target.isPresent()) {
                Approver approver = target.get();
                repo.save( approver.rejected() );           // 반려

                List<DtosApprover> DtoApprovers = approvers.stream().map(converter::getDtosApprover).collect(Collectors.toList());

                statusService.update(docs, DtoApprovers);   // 결재문서의 결재 상태 업데이트
                ongoingApprovalService.delete(docs);        // 반려 처리이므로 결재자 정보를 제거한다.

                countService.removeProceedDocsCntCache(SseApprovalType.REJECTED, docs);   // 결재문서 등록자에게 반려 알림.

                countService.removeApproverDocsCntCache( currentUserId, false );   // 반려 처리한 결재자의 카운트 초기화, 알림을 전송하지 않음.
                return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
            } else {
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재자 정보가 없습니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("rejected Exception.");
            log.warn("parameters: {}, user: {}", docs.toString(), currentUserId);
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재반려 에러입니다. 관리자에게 문의하세요");
        }
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재자 승인/반려 처리 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서 view page에서 필요한 결재자 정보 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    List<DtosApprover> getApprovers(Docs docs) {
        return repo.findByDocsIdOrderBySeq(docs.getId()).stream().map(e -> converter.getDtosApprover(e).setSeq( e.getSeq() + 1 )).collect(Collectors.toList());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서 view page에서 필요한 결재자 정보 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재자로 받은 결재문서의 기간 검색 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    List<DtosApprovalDocsMin> getDocsForApprover(DocsType type, LocalDate dateFrom, LocalDate dateTo, LoginUser loginUser) {
        List<Object[]> objList = repo.findForApprover(loginUser.getMember().getId(), type, dateFrom, dateTo);
        return objList.stream().map( e -> DtosApprovalDocsMinConverter.fromObjs(e) ).collect(Collectors.toList());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재자로 받은 결재문서의 기간 검색 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //




    
    List<DtosApprovalDocsMin> getApprovalDocs(Long writerId, DocsType type, LocalDate dateFrom, LocalDate dateTo, LoginUser loginUser) {
        if( ! authorityService.isAccessible(Menu.APPROVAL_SEARCH, loginUser) )
            return null;

        List<Object[]> objsList = repo.getApprovalDocs(type, dateFrom, dateTo);
        
        return objsList.stream().filter(
            // 작성자를 선택하지 않았으면 true로 처리해서 모두 집계, 아니면 작성자 일치하는 것만 집계, 일치하는 작성자를 위해서 nativeQuery 맨 끝에 writerId를 추가.
            e -> (writerId == null || writerId.equals(Long.valueOf(0L))) ? true : writerId.equals( Long.valueOf( ((BigInteger) e[11]).longValue() ))
        ).map( e -> DtosApprovalDocsMinConverter.fromObjs(e) ).collect(Collectors.toList());
    }

    
    List<DtosApprovalDocsMin> getApprovalDocs(Long docsId, LoginUser loginUser) {
        if( ! authorityService.isAccessible(Menu.APPROVAL_SEARCH, loginUser) )
            return null;

        List<Object[]> objsList = repo.getApprovalDocs(docsId);
        return objsList.stream().map( e -> DtosApprovalDocsMinConverter.fromObjs(e) ).collect(Collectors.toList());
    }
}
