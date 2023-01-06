package com.project.simplegw.document.approval.services;

import com.project.simplegw.document.approval.dtos.receive.DtorDefaultReport;
import com.project.simplegw.document.approval.dtos.receive.DtorTempDefaultReport;
import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsCommon;
import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.vos.DocsType;
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
public class DefaultReportService {
    private final ApprovalDocsService approvalDocsService;
    private final ApprovalDocsTempService tempService;

    @Autowired
    public DefaultReportService(ApprovalDocsService approvalDocsService, ApprovalDocsTempService tempService) {
        this.approvalDocsService = approvalDocsService;
        this.tempService = tempService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public ServiceMsg create(DocsType type, DtorDefaultReport dto, LoginUser loginUser) {
        DtorDocs dtorDocs = new DtorDocs().setTitle(dto.getTitle()).setContent(dto.getContent());
        return approvalDocsService.create(dtorDocs, type, dto.getArrApproverId(), dto.getArrReferrerId(), loginUser);
    }

    public ServiceMsg update(DocsType type, Long docsId, DtorDefaultReport dto, LoginUser loginUser) {
        DtorDocs dtorDocs = new DtorDocs().setTitle(dto.getTitle()).setContent(dto.getContent());
        return approvalDocsService.update(docsId, dtorDocs, type, dto.getArrApproverId(), dto.getArrReferrerId(), loginUser);
    }

    public ServiceMsg delete(DocsType type, Long docsId, LoginUser loginUser) {
        return approvalDocsService.delete(docsId, type, loginUser);
    }

    public DtosApprovalDocsCommon getDocs(DocsType type, Long docsId, LoginUser loginUser) {
        return approvalDocsService.getDocs(docsId, type, loginUser);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public ServiceMsg createTemp(DocsType type, DtorTempDefaultReport dto, LoginUser loginUser) {
        return tempService.createTemp(type, dto, loginUser);
    }


    public ServiceMsg updateTemp(DocsType type, Long docsId, DtorTempDefaultReport dto, LoginUser loginUser) {
        return tempService.updateTemp(type, docsId, dto, loginUser);
    }


    public ServiceMsg deleteTemp(DocsType type, Long docsId, LoginUser loginUser) {
        return tempService.deleteTemp(type, docsId, loginUser);
    }


    public DtosDocs getTemp(DocsType docsType, Long docsId) {
        return tempService.getTemp(docsType, docsId);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
