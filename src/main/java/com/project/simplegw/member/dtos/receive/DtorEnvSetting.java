package com.project.simplegw.member.dtos.receive;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorEnvSetting {
    @Min(value = 1L, message = "최소 1일 부터 최대 150일 사이로 입력하세요.")
    @Max(value = 150L, message = "최소 1일 부터 최대 150일 사이로 입력하세요.")
    @NotNull(message = "최소 1일 부터 최대 150일 까지로 입력하세요.")
    private int sysNotiDelDay;

    private boolean atOnceApprover;

    private boolean atOnceReferrer;

    private boolean atOnceApproval;

    private boolean mainCalendarMine;

    private boolean mainCalendarTeam;

    private boolean calendarHoliday;
}
