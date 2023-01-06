package com.project.simplegw.document.approval.dtos.receive.details;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.project.simplegw.document.approval.dtos.receive.DtorLineCommon;
import com.project.simplegw.system.vos.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class DtorDetailsCommon extends DtorLineCommon {
    // 결재문서 공통 양식: 제목, 결재자, 참조자
    // 각종 결재문서 전용 DTO에서 이 클래스를 상속하여 사용.
    @NotBlank(message = "제목을 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_TITLE, message = "제목을 " + Constants.COLUMN_LENGTH_TITLE + " 자 이하로 작성하세요.")
    private String title;
}
