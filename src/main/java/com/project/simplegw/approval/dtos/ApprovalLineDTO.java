package com.project.simplegw.approval.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApprovalLineDTO {
    // view --> server 로 결재라인 저장할 때 사용.
    private Long masterId;
    private String title;
    private Long[] arrApprover;
    private Long[] arrReferrer;
}
