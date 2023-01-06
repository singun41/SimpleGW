package com.project.simplegw.member.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosEnvSetting {
    private int sysNotiDelDay;
    private boolean atOnceApprover;
    private boolean atOnceReferrer;
    private boolean atOnceApproval;
    private boolean mainCalendarMine;
    private boolean mainCalendarTeam;
    private boolean calendarHoliday;
}
