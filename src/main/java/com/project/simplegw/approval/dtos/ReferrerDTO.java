package com.project.simplegw.approval.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ReferrerDTO {
    // 참조자 정보
    private Long memberId;
    private String team;
    private String name;
    private String jobTitle;
    private String status;
    private LocalDateTime checkedDatetime;
}
