package com.project.simplegw.document.helpers;

import com.project.simplegw.document.dtos.send.DtosComment;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.dtos.send.DtosDocsAddReferrer;
import com.project.simplegw.document.dtos.send.DtosDocsMin;
import com.project.simplegw.document.dtos.send.DtosDocsOptions;
import com.project.simplegw.document.dtos.send.DtosDocsTitle;
import com.project.simplegw.document.dtos.send.DtosTempDocs;
import com.project.simplegw.document.entities.Comment;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.entities.DocsOptions;
import com.project.simplegw.document.entities.TempDocs;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocsConverter {
    DtosDocsMin getDtosDocsMin(Docs entity);
    DtosDocsTitle getDtosDocsTitle(Docs entity);

    DtosDocs getDtosDocs(Docs entity);
    DtosComment getDtosComment(Comment entity);

    DtosTempDocs getDtosTempDocs(TempDocs entity);

    DtosDocs getDtosDocs(TempDocs entity);

    DtosDocsOptions getDtosDocsOptions(DocsOptions entity);

    DtosDocsAddReferrer getDtosDocsAddReferrer(DtosDocs dto);
}
