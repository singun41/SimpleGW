package com.project.simplegw.member.dtos.admin.receive;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorMemberAddOnUpdate {
    @Min(value = 0, message = "연차 지급 최소값은 0 입니다.")
    private double dayoffQty;
    private boolean updateDayoffQty;   // true 일 때 dayoffQty를 업데이트.

    @Min(value = 0, message = "연차 사용 최소값은 0 입니다.")
    private double dayfoffUse;
    private boolean updateDayoffUse;   // true 일 때 dayoffUse를 업데이트.
}
