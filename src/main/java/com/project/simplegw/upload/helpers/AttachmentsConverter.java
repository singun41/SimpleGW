package com.project.simplegw.upload.helpers;

import com.project.simplegw.upload.dtos.DtosAttachements;
import com.project.simplegw.upload.entities.Attachments;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttachmentsConverter {
    DtosAttachements getDto(Attachments entity);
}
