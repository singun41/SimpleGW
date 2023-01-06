package com.project.simplegw.postit.controllers;

import java.util.List;

import com.project.simplegw.postit.dtos.DtoPostIt;
import com.project.simplegw.postit.services.PostItService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/post-it")
public class PostItController {
    private final PostItService service;

    @Autowired
    public PostItController(PostItService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @GetMapping
    public ResponseEntity<Object> getList(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(service.getList(loginUser));
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody List<DtoPostIt> dtos, @AuthenticationPrincipal LoginUser loginUser) {
        service.save(dtos, loginUser);
        return ResponseConverter.ok(ResponseMsg.SAVED);
    }
}
