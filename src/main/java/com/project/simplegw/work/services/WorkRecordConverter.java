package com.project.simplegw.work.services;

import com.project.simplegw.work.dtos.WorkRecordDTO;
import com.project.simplegw.work.entities.WorkRecord;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface WorkRecordConverter {
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
    WorkRecordDTO getDTO(WorkRecord entity);
    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    WorkRecord getEntity(WorkRecordDTO dto);
    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
}
