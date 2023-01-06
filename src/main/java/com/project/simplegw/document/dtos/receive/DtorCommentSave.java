package com.project.simplegw.document.dtos.receive;

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
public class DtorCommentSave {
    @NotBlank(message = "내용을 작성하세요.")
    @Size(max = Constants.COLUMN_LENGTH_COMMENT, message = "댓글을 " + Constants.COLUMN_LENGTH_COMMENT + "자 이하로 작성하세요.")
    private String comment;
}
