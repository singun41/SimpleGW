package com.project.simplegw.approval.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString(exclude = "content")
@Accessors(chain = true)
public class ApprovalDocsDTO {
    // 결재문서 등록할 때 view --> server 전용. received only.
    private Long id;
    private String title;
    private String content;
    private Long[] approvers;
    private Long[] referrers;
    private Long writerId;
    private boolean registered;
}
