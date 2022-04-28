package com.project.simplegw.schedule.services;

import com.project.simplegw.schedule.dtos.ScheduleDTO;
import com.project.simplegw.schedule.entities.Schedule;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ScheduleConverter {
    ScheduleDTO getDto(Schedule entity);
    Schedule getEntity(ScheduleDTO dto);
}
