package com.project.simplegw.document.approval.dtos.send;

import com.project.simplegw.document.approval.vos.Sign;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosApprover {
    private int seq;
    private String team;
    private String jobTitle;
    private String name;
    private Long memberId;
    private Sign sign;
}
