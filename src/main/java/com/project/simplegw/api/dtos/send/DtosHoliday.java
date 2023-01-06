package com.project.simplegw.api.dtos.send;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosHoliday {
    private int index;
    private String title;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private boolean isHoliday;
}
