package com.project.simplegw.mobile.approval.services;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.system.security.SecurityUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile")
public class MobileApprovalController {
    
    private final MobileApprovalServices service;

    @Autowired
    public MobileApprovalController(MobileApprovalServices service) {
        this.service = service;
    }

    @GetMapping(path = "/dayoff/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsDTO> searchDayoff(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate from, @AuthenticationPrincipal SecurityUser loginUser) {
        return service.searchDayoff(from, loginUser.getMember().getId());
    }
}
