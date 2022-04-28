package com.project.simplegw.common.services;

import com.project.simplegw.common.dtos.BasecodeDTO;
import com.project.simplegw.common.dtos.CodeForAdminDTO;
import com.project.simplegw.common.entities.Basecode;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BasecodeConverter {
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    Basecode getEntity(BasecodeDTO dto);
    Basecode getEntity(CodeForAdminDTO dto);
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
    BasecodeDTO getDto(Basecode entity);
    CodeForAdminDTO getDtoForAdmin(Basecode entity);
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
}
