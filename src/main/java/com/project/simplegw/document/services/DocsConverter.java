package com.project.simplegw.document.services;

import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.document.dtos.DocsOptionsDTO;
import com.project.simplegw.document.entities.Content;
import com.project.simplegw.document.entities.DocsOptions;
import com.project.simplegw.document.entities.Document;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DocsConverter {

    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
    DocsDTO getDto(Document docs);

    DocsOptionsDTO getDto(DocsOptions entity);
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    Document getDocs(DocsDTO docsDTO);
    Content getContent(DocsDTO docsDTO);

    DocsOptions getOptions(DocsOptionsDTO dto);
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
}
