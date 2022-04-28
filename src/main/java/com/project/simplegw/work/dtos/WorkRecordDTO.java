package com.project.simplegw.work.dtos;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class WorkRecordDTO {
    private Long id;
    private LocalDate workDate;
    private String todayWork;
    private String nextWorkPlan;
    private String team;
    private String jobTitle;
    private String name;
}
