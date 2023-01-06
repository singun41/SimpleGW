package com.project.simplegw.document.approval.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsMin;
import com.project.simplegw.document.approval.dtos.send.DtosApprover;
import com.project.simplegw.document.approval.entities.ApprovalStatus;
import com.project.simplegw.document.approval.helpers.DtosApprovalDocsMinConverter;
import com.project.simplegw.document.approval.repositories.ApprovalStatusRepo;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.security.LoginUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ApprovalStatusService {
    private final ApprovalStatusRepo repo;

    @Autowired
    public ApprovalStatusService(ApprovalStatusRepo repo) {
        this.repo = repo;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서의 결재 상태 조회 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    ApprovalStatus getStatus(Docs docs) {
        return repo.findByDocsId(docs.getId()).orElseGet(ApprovalStatus::new);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서의 결재 상태 조회 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서의 결재 상태 등록 및 수정 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    void create(Docs docs, List<DtosApprover> approvers) throws Exception {   // ApproverService 에서 호출.
        repo.save( ApprovalStatus.builder().build().bindDocs(docs).init(approvers) );
    }

    void update(Docs docs, List<DtosApprover> approvers) throws Exception {   // ApproverService 에서 호출.
        repo.save( getStatus(docs).update(approvers) );
    }

    void delete(Docs docs) throws Exception {   // ApproverService 에서 호출.
        repo.findByDocsId(docs.getId()).ifPresent(repo::delete);
        repo.flush();   // 결재문서를 수정할 때 결재라인을 삭제하고 다시 insert하는데 docs_id 필드가 Unique Key라서 flush()로 delete를 완료시켜야 한다.
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재문서의 결재 상태 등록 및 수정 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 완결된 결재문서 조회 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    List<DtosApprovalDocsMin> getFinishedDocs(LocalDate dateFrom, LocalDate dateTo, DocsType type, LoginUser loginUser) {
        return repo.findFinished(loginUser.getMember().getId(), dateFrom, dateTo, type).stream()
            .map( DtosApprovalDocsMinConverter::fromObjs ).collect(Collectors.toList());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 완결된 결재문서 조회 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
