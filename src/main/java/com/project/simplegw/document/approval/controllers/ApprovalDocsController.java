package com.project.simplegw.document.approval.controllers;

import com.project.simplegw.document.approval.services.ApprovalCountService;
import com.project.simplegw.document.approval.services.ApprovalDocsService;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/approval")
public class ApprovalDocsController {
    private final ApprovalDocsService approvalDocsService;
    private final ApprovalCountService countService;

    @Autowired
    public ApprovalDocsController(ApprovalDocsService approvalDocsService, ApprovalCountService countService) {
        this.approvalDocsService = approvalDocsService;
        this.countService = countService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재자 승인/반려 처리 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @PatchMapping("/confirmed/{type}/{docsId}")
    public ResponseEntity<Object> confirmed(@PathVariable String type, @PathVariable Long docsId, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            approvalDocsService.confirmed(DocsType.valueOf(type.toUpperCase()), docsId, loginUser), ResponseMsg.CONFIRMED
        );
    }

    @PatchMapping("/rejected/{type}/{docsId}")
    public ResponseEntity<Object> rejected(@PathVariable String type, @PathVariable Long docsId, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            approvalDocsService.rejected(DocsType.valueOf(type.toUpperCase()), docsId, loginUser), ResponseMsg.REJECTED
        );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 결재자 승인/반려 처리 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //




    @GetMapping("/proceed-cnt")
    public ResponseEntity<Object> getProceedDocsCnt(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( countService.getProceedDocsCnt(loginUser) );
    }

    @GetMapping("/approver-cnt")
    public ResponseEntity<Object> getApproverDocsCnt(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( countService.getApproverDocsCnt(loginUser) );
    }

    @GetMapping("/referrer-cnt")
    public ResponseEntity<Object> getReferrerDocsCnt(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( countService.getReferrerDocsCnt(loginUser) );
    }




    // 삭제 처리: 모든 결재 문서 공통
    @DeleteMapping("/{type}/{docsId}")
    public ResponseEntity<Object> deleteApprovalDocs(@PathVariable Long docsId, @PathVariable String type, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            approvalDocsService.delete(docsId, DocsType.valueOf(type.toUpperCase()), loginUser), ResponseMsg.DELETED
        );
    }




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 모든 결재문서의 modify page에서 결재자/참조자 요청시 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/line/{docsId}/{type}")
    public ResponseEntity<Object> getDtosApprovalLinePack(@PathVariable Long docsId, @PathVariable String type, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok( approvalDocsService.getDtosApprovalLinePack(docsId, DocsType.valueOf(type.toUpperCase()), loginUser) );
    }


    // 참조자 추가
    @PatchMapping("/referrer-add/{type}/{docsId}")
    public ResponseEntity<Object> addReferrer(
        @PathVariable String type, @PathVariable Long docsId, @RequestBody Long[] arrReferrerId, @AuthenticationPrincipal LoginUser loginUser
    ) {
        return ResponseConverter.message(
            approvalDocsService.addReferrers(DocsType.valueOf(type.toUpperCase()), docsId, arrReferrerId, loginUser), ResponseMsg.SAVED
        );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 모든 결재문서의 modify page에서 결재자/참조자 요청시 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
