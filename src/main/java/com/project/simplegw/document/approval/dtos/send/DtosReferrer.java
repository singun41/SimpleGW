package com.project.simplegw.document.approval.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosReferrer {
    private String jobTitle;
    private String name;
    private Long memberId;
    private boolean checked;
}
