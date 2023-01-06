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
@ToString(exclude = "content")
public class DtorDocs {   // 문서 생성, 수정시 프론트로부터 받는 데이터 dto, 업데이트시 docsId는 url로 받는다. (RestAPI 표준 설계를 따름.)
    @NotBlank(message = "제목을 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_TITLE, message = "제목을 " + Constants.COLUMN_LENGTH_TITLE + " 자 이하로 작성하세요.")
    private String title;

    @NotBlank(message = "내용을 입력하세요.")
    private String content;
}
