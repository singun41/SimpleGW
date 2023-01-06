package com.project.simplegw.document.approval.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsMin;
import com.project.simplegw.document.approval.entities.Referrer;
import com.project.simplegw.document.approval.helpers.ApprovalConverter;
import com.project.simplegw.document.approval.vos.ApprovalRole;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.vos.DocsGroup;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.security.LoginUser;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ApprovalListService {
    private final OngoingApprovalService ongoingApprovalService;
    private final ApprovalStatusService approvalStatusService;
    private final ApproverService approverService;
    private final ReferrerService referrerService;
    private final ApprovalConverter converter;

    @Autowired
    public ApprovalListService(
        OngoingApprovalService ongoingApprovalService, ApprovalStatusService approvalStatusService,
        ApproverService approverService, ReferrerService referrerService, ApprovalConverter converter
    ) {
        this.ongoingApprovalService = ongoingApprovalService;
        this.approvalStatusService = approvalStatusService;
        this.approverService = approverService;
        this.referrerService = referrerService;
        this.converter = converter;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    public List<DtosApprovalDocsMin> getReceivedList(ApprovalRole role, LoginUser loginUser) {
        if(role == ApprovalRole.APPROVER)
            return ongoingApprovalService.getReceivedList(loginUser).stream().map(e -> {
                Docs docs = e.getDocs();
                return converter.getDtosApprovalDocsMin(approvalStatusService.getStatus(docs))
                    .setId(docs.getId()).updateDocsType(docs.getType()).setTitle(docs.getTitle()).setCreatedDate(docs.getCreatedDate())
                    .setWriterTeam(docs.getWriterTeam()).setWriterJobTitle(docs.getWriterJobTitle()).setWriterName(docs.getWriterName());
            }).collect(Collectors.toList());

        
        else if(role == ApprovalRole.REFERRER)
            return referrerService.getUncheckedReferrers(loginUser).stream()
                .map(Referrer::getDocs)
                .filter(e -> e.getType().getGroup() == DocsGroup.APPROVAL)   // 일반문서 공유기능을 같은 Referrer를 이용하기 때문에 결재문서만 필터링한다.
                .map(e -> converter.getDtosApprovalDocsMin(approvalStatusService.getStatus(e))
                    .setId(e.getId()).updateDocsType(e.getType()).setTitle(e.getTitle()).setCreatedDate(e.getCreatedDate())
                    .setWriterTeam(e.getWriterTeam()).setWriterJobTitle(e.getWriterJobTitle()).setWriterName(e.getWriterName())
                ).collect(Collectors.toList());

            // return referrerService.getUncheckedReferrers(loginUser).stream().map(e -> {
            //     Docs docs = e.getDocs();
            //     return converter.getDtosApprovalDocsMin(approvalStatusService.getStatus(docs))
            //         .setId(docs.getId()).updateDocsType(docs.getType()).setTitle(docs.getTitle()).setCreatedDate(docs.getCreatedDate())
            //         .setWriterTeam(docs.getWriterTeam()).setWriterJobTitle(docs.getWriterJobTitle()).setWriterName(docs.getWriterName());
            // }).collect(Collectors.toList());
        
        
        else
            return new ArrayList<DtosApprovalDocsMin>();
    }





    public List<DtosApprovalDocsMin> getDocsForApprover(DocsType type, LocalDate dateFrom, LocalDate dateTo, LoginUser loginUser) {
        return approverService.getDocsForApprover(type, dateFrom, dateTo, loginUser);
    }


    public List<DtosApprovalDocsMin> getDocsForReferrer(DocsType type, LocalDate dateFrom, LocalDate dateTo, LoginUser loginUser) {
        return referrerService.getDocsForReferrer(type, dateFrom, dateTo, loginUser);
    }


    public List<DtosApprovalDocsMin> getApprovalDocs(Long writerId, DocsType type, LocalDate dateFrom, LocalDate dateTo, LoginUser loginUser) {
        return approverService.getApprovalDocs(writerId, type, dateFrom, dateTo, loginUser);
    }

    public List<DtosApprovalDocsMin> getApprovalDocs(Long docsId, LoginUser loginUser) {
        return approverService.getApprovalDocs(docsId, loginUser);
    }


    
    public List<DtosApprovalDocsMin> getProceedApprovalDocs(LoginUser loginUser) {
        return ongoingApprovalService.getProceedList(loginUser).stream().map(e -> {
            Docs docs = e.getDocs();
            return converter.getDtosApprovalDocsMin(approvalStatusService.getStatus(docs))
                .setId(docs.getId()).updateDocsType(docs.getType()).setTitle(docs.getTitle()).setCreatedDate(docs.getCreatedDate());
        }).collect(Collectors.toList());
    }


    public List<DtosApprovalDocsMin> getFinishedDocs(LocalDate dateFrom, LocalDate dateTo, DocsType type, LoginUser loginUser) {
        return approvalStatusService.getFinishedDocs(dateFrom, dateTo, type, loginUser);
    }
}
