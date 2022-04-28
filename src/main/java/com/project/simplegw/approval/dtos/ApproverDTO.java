package com.project.simplegw.approval.dtos;

import java.time.LocalDateTime;

import com.project.simplegw.approval.vos.ApproverStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ApproverDTO {
    // 결재자 정보
    private Long memberId;
    private int seq;
    private String team;
    private String name;
    private String jobTitle;
    private ApproverStatus status;
    private LocalDateTime checkedDatetime;
}
