package com.project.simplegw.schedule.dtos.send;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true, exclude = "content")
public class DtosSchedule extends DtosScheduleMin {
    private String content;
    private LocalDateTime createdDatetime;
}
