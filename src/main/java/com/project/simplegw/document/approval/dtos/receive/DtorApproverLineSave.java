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
public class DtorApproverLineSave extends DtorLineCommon {
    // 결재라인 설정 화면에서 결재라인 저장시 프론트로부터 받는 dto

    @NotBlank(message = "결재라인 타이틀을 작성하세요.")
    @Size(max = Constants.COLUMN_LENGTH_TITLE, message = "결재라인 타이틀을 " + Constants.COLUMN_LENGTH_TITLE + " 자 이하로 작성하세요.")
    private String title;
}
