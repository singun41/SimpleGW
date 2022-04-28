package com.project.simplegw.schedule.dtos;

import java.time.LocalDateTime;

import com.project.simplegw.schedule.vos.ScheduleType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ScheduleDTO {
    private Long id;
    private Long memberId;
    private ScheduleType type;
    private String code;
    private String codeValue;
    private String team;
    private String jobTitle;
    private String name;
    private String title;
    private String content;
    private LocalDateTime datetimeStart;
    private LocalDateTime datetimeEnd;
    private LocalDateTime createdDatetime;
}
