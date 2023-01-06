package com.project.simplegw.schedule.helpers;

import org.mapstruct.Mapper;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.schedule.dtos.admin.send.DtosColor;
import com.project.simplegw.schedule.dtos.receive.DtorSchedule;
import com.project.simplegw.schedule.dtos.receive.DtorScheduleIncludedTime;
import com.project.simplegw.schedule.dtos.send.DtosSchedule;
import com.project.simplegw.schedule.dtos.send.DtosScheduleMember;
import com.project.simplegw.schedule.dtos.send.DtosScheduleMin;
import com.project.simplegw.schedule.entities.Color;
import com.project.simplegw.schedule.entities.Schedule;

@Mapper(componentModel = "spring")
public interface ScheduleConverter {
    Schedule getEntity(DtorSchedule dto);
    Schedule getEntity(DtorScheduleIncludedTime dto);

    DtosColor getDtosColor(Color entity);
    DtosScheduleMin getDtosScheduleMin(Schedule entity);
    DtosSchedule getDtosSchedule(Schedule entity);

    DtosScheduleMember getScheduleMember(MemberData data);
}
