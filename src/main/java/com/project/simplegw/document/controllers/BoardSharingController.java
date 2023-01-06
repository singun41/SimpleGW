package com.project.simplegw.document.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.document.approval.services.ReferrerService;
import com.project.simplegw.document.services.DocsService;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/board")
public class BoardSharingController {
    private final DocsService docsService;
    private final ReferrerService referrerService;

    @Autowired
    public BoardSharingController(DocsService docsService, ReferrerService referrerService) {
        this.docsService = docsService;
        this.referrerService = referrerService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    // 참조자 추가
    @PatchMapping("/referrer-add/{type}/{docsId}")
    public ResponseEntity<Object> addReferrer(
        @PathVariable String type, @PathVariable Long docsId, @RequestBody Long[] arrReferrerId, @AuthenticationPrincipal LoginUser loginUser
    ) {
        return ResponseConverter.message(
            referrerService.add( docsService.getDocsEntity(docsId, DocsType.valueOf(type.toUpperCase())), arrReferrerId, loginUser ), ResponseMsg.SAVED
        );
    }
}
