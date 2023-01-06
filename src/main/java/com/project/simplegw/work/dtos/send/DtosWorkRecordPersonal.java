package com.project.simplegw.work.dtos.send;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosWorkRecordPersonal {
    private LocalDate workDate;
    private String todayWork;
    private String nextPlan;
}
