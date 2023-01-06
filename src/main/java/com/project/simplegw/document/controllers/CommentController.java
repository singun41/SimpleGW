package com.project.simplegw.document.controllers;

import com.project.simplegw.document.dtos.receive.DtorCommentSave;
import com.project.simplegw.document.services.CommentService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService service;

    @Autowired
    public CommentController(CommentService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    @GetMapping("/{docsId}")
    public ResponseEntity<Object> getComments(@PathVariable Long docsId) {
        return ResponseConverter.ok(service.getComments(docsId));
    }

    @PostMapping("/{type}/{docsId}")
    public ResponseEntity<Object> save(@PathVariable String type, @PathVariable Long docsId, @Validated @RequestBody DtorCommentSave dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.save(DocsType.valueOf(type.toUpperCase()), docsId, dto, loginUser), ResponseMsg.INSERTED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            service.delete(id, loginUser), ResponseMsg.DELETED
        );
    }
}
