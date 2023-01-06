package com.project.simplegw.document.approval.helpers.details;

import org.mapstruct.Mapper;

import com.project.simplegw.document.approval.dtos.receive.details.dayoff.DtorDayoff;
import com.project.simplegw.document.approval.dtos.receive.details.dayoff.DtorDayoffDetails;
import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsCommon;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosDayoff;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosDayoffDetails;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosTempDayoff;
import com.project.simplegw.document.approval.entities.details.dayoff.Dayoff;
import com.project.simplegw.document.approval.entities.details.dayoff.TempDayoff;
import com.project.simplegw.document.dtos.receive.DtorDocs;
import com.project.simplegw.document.dtos.send.DtosDocs;

@Mapper(componentModel = "spring")
public interface DayoffConverter {
    DtorDocs getDtorDocs(DtorDayoff dto);
    Dayoff getEntity(DtorDayoffDetails dto);
    
    DtosDayoffDetails getDetails(Dayoff entity);
    DtosDayoff getDocs(DtosApprovalDocsCommon dto);

    TempDayoff getTempEntity(DtorDayoffDetails dto);
    DtosTempDayoff getDtosTempDayoff(DtosDocs dto);
    DtosDayoffDetails getDetails(TempDayoff entity);
}
