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
@ToString(exclude = "content")
public class DtorTempDefaultReport {
    // 모든 결재문서 임시저장용 공통 dto, 임시저장은 결재라인을 저장하지 않는다.
    // 다른 디테일이 있는 결재문서의 경우 이 클래스를 상속한 뒤 디테일 클래스를 리스트로 추가해 사용한다.

    @NotBlank(message = "제목을 작성하세요.")
    @Size(max = Constants.COLUMN_LENGTH_TITLE, message = "제목을 " + Constants.COLUMN_LENGTH_TITLE + " 자 이하로 작성하세요.")
    private String title;

    @NotBlank(message = "내용을 입력하세요.")
    private String content;
}
