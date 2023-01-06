package com.project.simplegw.alarm.dtos.send;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosAlarm {
    private Long id;
    private LocalDate alarmDate;
    private LocalTime alarmTime;
    private String title;
    private String remarks;
    private LocalDateTime createdDatetime;
}
