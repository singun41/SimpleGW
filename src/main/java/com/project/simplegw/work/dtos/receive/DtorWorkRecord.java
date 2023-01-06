package com.project.simplegw.work.dtos.receive;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.project.simplegw.system.helpers.DateValid;
import com.project.simplegw.system.vos.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorWorkRecord {
    @NotBlank(message = "작성일자를 확인하세요.")
    @DateValid   // null 허용
    private String workDate;

    @Size(max = Constants.COLUMN_LENGTH_COMMENT, message = "업무 처리 내용을 " + Constants.COLUMN_LENGTH_COMMENT + " 자 이하로 작성하세요.")
    @NotBlank(message = "업무 처리 내용을 작성하세요.")
    private String todayWork;

    // null or 공백 허용
    @Size(max = Constants.COLUMN_LENGTH_COMMENT, message = "다음 업무 계획을 " + Constants.COLUMN_LENGTH_COMMENT + " 자 이하로 작성하세요.")
    private String nextPlan;
}
