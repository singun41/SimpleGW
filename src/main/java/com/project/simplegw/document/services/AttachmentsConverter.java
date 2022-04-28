package com.project.simplegw.document.services;

import com.project.simplegw.document.dtos.AttachmentsDTO;
import com.project.simplegw.document.entities.Attachments;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AttachmentsConverter {
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    Attachments getEntity(AttachmentsDTO dto);
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
    AttachmentsDTO getDTO(Attachments entity);
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
}
