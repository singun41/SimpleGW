package com.project.simplegw.schedule.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosScheduleSummary {
    private long dayoff;
    private long half;   // halfAm + halfPm
    private long outOnBusiness;
    private long businessTrip;
    private long education;
}
