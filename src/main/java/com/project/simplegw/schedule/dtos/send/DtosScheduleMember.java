package com.project.simplegw.schedule.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosScheduleMember {
    private String type;
    private String team;
    private String jobTitle;
    private String name;
}
