package com.project.simplegw.document.services;

import com.project.simplegw.document.dtos.CommentDTO;
import com.project.simplegw.document.entities.Comment;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CommentConverter {
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
    CommentDTO getDto(Comment entity);
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    Comment getEntity(CommentDTO dto);
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
}
