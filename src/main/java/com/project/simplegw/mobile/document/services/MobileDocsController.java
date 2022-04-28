package com.project.simplegw.mobile.document.services;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.document.dtos.DocsDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile")
public class MobileDocsController {
    
    private final MobileDocsService service;
    // private final DocsService service;

    @Autowired
    public MobileDocsController(MobileDocsService service) {
        this.service = service;
    }

    @GetMapping(path = "/notice/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsDTO> searchNotice(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate from) {
        return service.searchNotice(from);
    }
}
