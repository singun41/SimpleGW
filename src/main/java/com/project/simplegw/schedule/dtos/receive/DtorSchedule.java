package com.project.simplegw.schedule.dtos.receive;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class DtorSchedule {
    @NotNull(message = "날짜를 입력하세요.")   // @NotBlank는 오류발생. null 체크만 하는 NotNull을 사용한다.
    @DateValid   // null 허용되므로 위에서 null 체크
    private LocalDate dateFrom;
    
    @NotNull(message = "날짜를 입력하세요.")   // @NotBlank는 오류발생. null 체크만 하는 NotNull을 사용한다.
    @DateValid   // null 허용되므로 위에서 null 체크
    private LocalDate dateTo;

    @NotBlank(message = "일정 구분 코드를 선택하세요.")
    private String code;

    @NotBlank(message = "제목을 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_TITLE, message = "제목을 " + Constants.COLUMN_LENGTH_TITLE + " 자 이하로 작성하세요.")
    private String title;

    // null 허용
    @Size(max = Constants.COLUMN_LENGTH_REMARKS, message = "내용을 " + Constants.COLUMN_LENGTH_REMARKS + " 자 이하로 작성하세요.")
    private String content;
}
