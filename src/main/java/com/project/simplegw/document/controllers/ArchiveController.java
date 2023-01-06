package com.project.simplegw.document.controllers;

import java.time.LocalDate;

import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.services.ArchiveService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/archive")
public class ArchiveController {
    private final ArchiveService service;

    @Autowired
    public ArchiveController(ArchiveService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    
    @GetMapping(path = "/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> getList(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo) {
        return ResponseConverter.ok(service.getList(dateFrom, dateTo));
    }





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody DtorDocs dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.create(dto, loginUser), ResponseMsg.INSERTED
        );
    }

    @PatchMapping("/{docsId}")
    public ResponseEntity<Object> update(@PathVariable Long docsId, @Validated @RequestBody DtorDocs dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.update(docsId, dto, loginUser), ResponseMsg.UPDATED
        );
    }

    @DeleteMapping("/{docsId}")
    public ResponseEntity<Object> delete(@PathVariable Long docsId, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            service.delete(docsId, loginUser), ResponseMsg.DELETED
        );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    // Archive(자료실)은 임시저장 기능을 제공하지 않는다.
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
