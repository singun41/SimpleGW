package com.project.simplegw.document.approval.dtos.receive;

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
@ToString(callSuper = true)
public class DtorDefaultReport extends DtorLineCommon {
    @NotBlank(message = "제목을 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_TITLE, message = "제목을 " + Constants.COLUMN_LENGTH_TITLE + " 자 이하로 작성하세요.")
    private String title;

    @NotBlank(message = "내용을 입력하세요.")
    private String content;
}
