package com.project.simplegw.schedule.dtos.admin.receive;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.project.simplegw.system.vos.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorColor {
    @NotBlank(message = "코드값이 누락되었습니다.")
    @Size(min = Constants.COLUMN_LENGTH_BASECODE_CODE, max = Constants.COLUMN_LENGTH_BASECODE_CODE, message = "코드값이 올바르지 않습니다.")
    private String code;

    @NotBlank(message = "색상 코드값을 hex 값으로 입력하세요.")
    @Size(min = 6, max = 6, message = "색상 코드값을 hex 값으로 입력하세요.")
    private String hexValue;
}
