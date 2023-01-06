package com.project.simplegw.document.approval.controllers;

import com.project.simplegw.document.approval.dtos.receive.DtorDefaultReport;
import com.project.simplegw.document.approval.dtos.receive.DtorTempDefaultReport;
import com.project.simplegw.document.approval.services.DefaultReportService;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/approval/forms")
public class DefaultReportController {
    private final DefaultReportService service;

    @Autowired
    public DefaultReportController(DefaultReportService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    // 기본 결재문서 형식으로 사용하는 공통 컨트롤러, DocsType을 String으로 url에 포함시켜 받고 Enum으로 전환해서 서비스로 넘긴다.


    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @PostMapping("/{type}")
    public ResponseEntity<Object> create(@PathVariable String type, @Validated @RequestBody DtorDefaultReport dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.create(DocsType.valueOf(type.toUpperCase()), dto, loginUser), ResponseMsg.INSERTED
        );
    }


    @PatchMapping("/{type}/{docsId}")
    public ResponseEntity<Object> update(
        @PathVariable String type, @PathVariable Long docsId,
        @Validated @RequestBody DtorDefaultReport dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser
    ) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.update(DocsType.valueOf(type.toUpperCase()), docsId, dto, loginUser), ResponseMsg.UPDATED
        );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @PostMapping("/{type}/temp")
    public ResponseEntity<Object> createTemp(@PathVariable String type, @Validated @RequestBody DtorTempDefaultReport dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.createTemp(DocsType.valueOf(type.toUpperCase()), dto, loginUser), ResponseMsg.INSERTED
        );
    }

    @PatchMapping("/{type}/temp/{docsId}")
    public ResponseEntity<Object> updateTemp(
        @PathVariable String type, @PathVariable Long docsId,
        @Validated @RequestBody DtorTempDefaultReport dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser
    ) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.updateTemp(DocsType.valueOf(type.toUpperCase()), docsId, dto, loginUser), ResponseMsg.UPDATED
        );
    }

    @DeleteMapping("/{type}/temp/{docsId}")
    public ResponseEntity<Object> deleteTemp(@PathVariable String type, @PathVariable Long docsId, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            service.deleteTemp(DocsType.valueOf(type.toUpperCase()), docsId, loginUser), ResponseMsg.DELETED
        );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
