package com.project.simplegw.document.approval.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.document.approval.services.ApprovalListService;
import com.project.simplegw.document.approval.vos.ApprovalRole;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/approval-list")
public class ApprovalListController {
    private final ApprovalListService service;

    @Autowired
    public ApprovalListController(ApprovalListService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    @GetMapping("/approver/new")
    public ResponseEntity<Object> getReceivedListForApprover(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(
            service.getReceivedList(ApprovalRole.APPROVER, loginUser)
        );
    }


    @GetMapping("/referrer/new")
    public ResponseEntity<Object> getReceivedListForReferrer(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(
            service.getReceivedList(ApprovalRole.REFERRER, loginUser)
        );
    }




    @GetMapping(path = "/approver", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> getReceivedListForApprover(@RequestParam String type,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        return ResponseConverter.ok(
            service.getDocsForApprover(DocsType.valueOf(type.toUpperCase()), dateFrom, dateTo, loginUser)
        );
    }


    @GetMapping(path = "/referrer", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> getReceivedListForReferrer(@RequestParam String type,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        return ResponseConverter.ok(
            service.getDocsForReferrer(DocsType.valueOf(type.toUpperCase()), dateFrom, dateTo, loginUser)
        );
    }


    @GetMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> getApprovalDocs(@RequestParam Long writerId, @RequestParam String type,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        return ResponseConverter.ok(
            service.getApprovalDocs(writerId, DocsType.valueOf(type.toUpperCase()), dateFrom, dateTo, loginUser)
        );
    }

    @GetMapping(path = "/no", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> getApprovalDocs(@RequestParam Long docsId, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(
            service.getApprovalDocs(docsId, loginUser)
        );
    }


    @GetMapping("/proceed")
    public ResponseEntity<Object> getProceedApprovalDocs(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(
            service.getProceedApprovalDocs(loginUser)
        );
    }


    @GetMapping(path = "/finished", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> getFinishedDocs(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo,    
        @RequestParam String type, @AuthenticationPrincipal LoginUser loginUser
    ) {
        return ResponseConverter.ok(
            service.getFinishedDocs(dateFrom, dateTo, DocsType.valueOf(type.toUpperCase()), loginUser)
        );
    }
}
