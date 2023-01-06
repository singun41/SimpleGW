package com.project.simplegw.schedule.dtos.send;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosScheduleMin {
    private Long id;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private LocalTime timeFrom;
    private LocalTime timeTo;

    private String title;
    private String code;
    private String value;
    
    private Long memberId;
    private String team;
    private String jobTitle;
    private String name;

    private String colorHex;

    private boolean mine;
}
