package com.project.simplegw.alarm.helpers;

import org.mapstruct.Mapper;

import com.project.simplegw.alarm.dtos.receive.DtorAlarm;
import com.project.simplegw.alarm.dtos.send.DtosAlarm;
import com.project.simplegw.alarm.entities.Alarm;

@Mapper(componentModel = "spring")
public interface AlarmConverter {
    Alarm getEntity(DtorAlarm dto);
    DtosAlarm getDto(Alarm entity);
}
