package com.project.simplegw.work.helpers;

import org.mapstruct.Mapper;

import com.project.simplegw.work.dtos.receive.DtorWorkRecord;
import com.project.simplegw.work.dtos.send.DtosWorkRecordForList;
import com.project.simplegw.work.dtos.send.DtosWorkRecordPersonal;
import com.project.simplegw.work.entities.WorkRecord;

@Mapper(componentModel = "spring")
public interface WorkRecordConverter {
    DtosWorkRecordPersonal getDtosWorkRecordPersonal(WorkRecord entity);
    DtosWorkRecordForList getDtosWorkRecordForList(WorkRecord entity);
    WorkRecord getEntity(DtorWorkRecord dto);
}
