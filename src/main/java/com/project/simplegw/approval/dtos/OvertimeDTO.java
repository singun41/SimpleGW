package com.project.simplegw.approval.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OvertimeDTO {
    private int seq;
    private Long memberId;
    private String code;
    private String value;
    private String team;
    private String jobTitle;
    private String name;
    private LocalDate workDate;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private String remarks;
}
