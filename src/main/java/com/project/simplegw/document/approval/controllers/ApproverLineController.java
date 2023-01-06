package com.project.simplegw.document.approval.controllers;

import com.project.simplegw.document.approval.dtos.receive.DtorApproverLineSave;
import com.project.simplegw.document.approval.services.ApproverLineService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/approver-line")
public class ApproverLineController {
    private final ApproverLineService lineService;

    @Autowired
    public ApproverLineController(ApproverLineService lineService) {
        this.lineService = lineService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @GetMapping
    public ResponseEntity<Object> getSavedLines(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.ok(lineService.getLines(loginUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSavedLineDetails(@PathVariable Long id) {
        return ResponseConverter.ok(lineService.getLineDetails(id));
    }

    @PostMapping
    public ResponseEntity<Object> saveLines(@Validated @RequestBody DtorApproverLineSave dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);

        return ResponseConverter.message(
            lineService.saveLines(dto, loginUser), ResponseMsg.INSERTED
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateLines(@PathVariable Long id, @Validated @RequestBody DtorApproverLineSave dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);

        return ResponseConverter.message(
            lineService.updateLines(id, dto, loginUser), ResponseMsg.UPDATED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteLines(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message(
            lineService.deleteLines(id, loginUser), ResponseMsg.DELETED
        );
    }
}
