package com.project.simplegw.document.approval.dtos.send.details.dayoff;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosDayoffDetails {
    private int seq;
    private String code;
    private String value;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private int duration;
    private double count;
}
