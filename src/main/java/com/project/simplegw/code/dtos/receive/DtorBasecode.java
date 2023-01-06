package com.project.simplegw.code.dtos.receive;

import javax.validation.constraints.Min;
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
public class DtorBasecode {
    @Min(value = 1, message = "정렬 순서를 입력하세요.")
    private int seq;

    @NotBlank(message = "코드를 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_BASECODE_CODE, message = "코드를 " + Constants.COLUMN_LENGTH_TITLE + " 자 이하로 작성하세요.")
    private String code;
    
    @NotBlank(message = "값을 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_BASECODE_VALUE, message = "값을 " + Constants.COLUMN_LENGTH_BASECODE_VALUE + " 자 이하로 작성하세요.")
    private String value;

    @Size(max = Constants.COLUMN_LENGTH_REMARKS, message = "비고를 " + Constants.COLUMN_LENGTH_REMARKS + " 자 이하로 작성하세요.")
    private String remarks;   // null 허용

    private boolean enabled;
}
