package com.project.simplegw.schedule.dtos.receive;

import java.time.LocalTime;

import javax.validation.constraints.NotNull;

import com.project.simplegw.system.helpers.TimeValid;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class DtorScheduleIncludedTime extends DtorSchedule {
    @NotNull(message = "시작 시간을 입력하세요.")   // @NotBlank는 오류발생. null 체크만 하는 NotNull을 사용한다.
    @TimeValid
    private LocalTime timeFrom;
    
    @NotNull(message = "종료 시간을 입력하세요.")   // @NotBlank는 오류발생. null 체크만 하는 NotNull을 사용한다.
    @TimeValid
    private LocalTime timeTo;
}
