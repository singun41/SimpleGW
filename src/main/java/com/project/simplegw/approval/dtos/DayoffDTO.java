package com.project.simplegw.approval.dtos;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DayoffDTO {
    private int seq;
    private String code;
    private String value;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private int duration;
    private double count;
}
