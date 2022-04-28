package com.project.simplegw.mobile.document.services;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.document.dtos.DocsSearchDTO;
import com.project.simplegw.document.services.DocsService;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileDocsService {
    
    private final DocsService docsService;

    @Autowired
    public MobileDocsService(DocsService docsService) {
        this.docsService = docsService;
    }

    public List<DocsDTO> searchNotice(LocalDate from) {
        return docsService.getDocsDtoList(new DocsSearchDTO().setDateStart(from).setDateEnd(LocalDate.now()).setType(DocumentType.BOARD).setKind(DocumentKind.NOTICE).setRegistered(true));
    }

    public DocsDTO getDocsDto(Long docsId) {
        return docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.NOTICE);
    }
}
