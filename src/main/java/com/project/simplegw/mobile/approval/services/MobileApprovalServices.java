package com.project.simplegw.mobile.approval.services;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.approval.services.ApprovalService;
import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.document.dtos.DocsSearchDTO;
import com.project.simplegw.document.services.DocsService;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileApprovalServices {
    
    private final DocsService docsService;
    private final ApprovalService approvalService;
    
    @Autowired
    public MobileApprovalServices(DocsService docsService, ApprovalService approvalService) {
        this.docsService = docsService;
        this.approvalService = approvalService;
    }

    public List<DocsDTO> searchDayoff(LocalDate from, Long memberId) {
        return docsService.getDocsDtoList(new DocsSearchDTO().setDateStart(from).setDateEnd(LocalDate.now()).setType(DocumentType.APPROVAL).setKind(DocumentKind.DAYOFF).setRegistered(true), memberId);
    }
}
