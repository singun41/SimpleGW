package com.project.simplegw.document.approval.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.simplegw.document.approval.dtos.receive.DtorApproverLineSave;
import com.project.simplegw.document.approval.dtos.send.DtosMember;
import com.project.simplegw.document.approval.dtos.send.DtosSavedLine;
import com.project.simplegw.document.approval.dtos.send.DtosSavedLineDetails;
import com.project.simplegw.document.approval.entities.ApproverLine;
import com.project.simplegw.document.approval.entities.ApproverLineDetails;
import com.project.simplegw.document.approval.helpers.ApprovalConverter;
import com.project.simplegw.document.approval.repositories.ApproverLineDetailsRepo;
import com.project.simplegw.document.approval.repositories.ApproverLineRepo;
import com.project.simplegw.document.approval.vos.ApprovalRole;
import com.project.simplegw.member.entities.Member;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.system.security.LoginUser;
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
public class ApproverLineService {
    private final ApproverLineRepo lineRepo;
    private final ApproverLineDetailsRepo lineDetailsRepo;
    private final ApprovalConverter approvalConverter;
    private final MemberService memberService;

    @Autowired
    public ApproverLineService(
        ApproverLineRepo lineRepo, ApproverLineDetailsRepo lineDetailsRepo, ApprovalConverter approvalConverter, MemberService memberService
    ) {
        this.lineRepo = lineRepo;
        this.lineDetailsRepo = lineDetailsRepo;
        this.approvalConverter = approvalConverter;
        this.memberService = memberService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 저장된 결재라인 정보(결재문서x) ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public List<DtosSavedLine> getLines(LoginUser loginUser) {
        return lineRepo.findByOwnerId( loginUser.getMember().getId() ).stream().map(approvalConverter::getDto).collect(Collectors.toList());
    }

    public DtosSavedLineDetails getLineDetails(Long approverLineId) {
        List<ApproverLineDetails> savedList = lineDetailsRepo.findByMasterId(approverLineId);
        
        if(savedList.size() == 0)
            return null;

        List<DtosMember> approvers = new ArrayList<>();
        List<DtosMember> referrers = new ArrayList<>();

        savedList.forEach(e -> {
            if(ApprovalRole.APPROVER == e.getRole())
                approvers.add( approvalConverter.getDto( memberService.getMemberData(e.getMemberId()) ) );

            if(ApprovalRole.REFERRER == e.getRole())
                referrers.add( approvalConverter.getDto( memberService.getMemberData(e.getMemberId()) ) );
        });

        return new DtosSavedLineDetails().setApprovers(approvers).setReferrers(referrers);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 저장된 결재라인 정보(결재문서x) ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재라인 설정 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public ServiceMsg saveLines(DtorApproverLineSave dto, LoginUser loginUser) {
        try {
            Member owner = loginUser.getMember();
            List<ApproverLineDetails> detailsEntities = new ArrayList<>();
    
            ApproverLine master = lineRepo.save( ApproverLine.builder().owner(owner).title(dto.getTitle()).build() );

            List<Long> approvers = Arrays.asList(dto.getArrApproverId());
            approvers.forEach(e -> {
                ApproverLineDetails approver = ApproverLineDetails.builder().master(master).role(ApprovalRole.APPROVER).seq(approvers.indexOf(e) + 1).memberId(e).build();
                detailsEntities.add(approver);
            });

            List<Long> referrers = Arrays.asList(dto.getArrReferrerId());
            referrers.forEach(e -> {
                ApproverLineDetails referrer = ApproverLineDetails.builder().master(master).role(ApprovalRole.REFERRER).seq(0).memberId(e).build();
                detailsEntities.add(referrer);
            });
    
            lineDetailsRepo.saveAll(detailsEntities);
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
        } catch(Exception e) {
            e.printStackTrace();
            log.warn("saveLines Exception.");
            log.warn("parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재라인 템플릿 저장 에러입니다. 관리자에게 문의하세요.");
        }
    }



    public ServiceMsg deleteLines(Long lineId, LoginUser loginUser) {
        try {
            Optional<ApproverLine> target = lineRepo.findById(lineId);

            if(target.isPresent()) {
                ApproverLine line = target.get();
                if(line.getOwner().equals( loginUser.getMember() )) {
                    lineRepo.delete(line);
                    return new ServiceMsg().setResult(ServiceResult.SUCCESS);
                
                } else {
                    return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("저장된 결재라인의 소유자가 아닙니다.");
                }

            } else {
                return new ServiceMsg().setResult(ServiceResult.FAILURE);
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("deleteLines Exception.");
            log.warn("parameters: {}", lineId.toString());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("결재라인 템플릿 삭제 에러입니다. 관리자에게 문의하세요.");
        }
    }



    public ServiceMsg updateLines(Long lineId, DtorApproverLineSave dto, LoginUser loginUser) {
        // update = delete and save
        ServiceMsg deleteResult = deleteLines(lineId, loginUser);
        if(deleteResult.getResult() == ServiceResult.SUCCESS)
            return saveLines(dto, loginUser);
        else
            return deleteResult;
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재라인 설정 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
