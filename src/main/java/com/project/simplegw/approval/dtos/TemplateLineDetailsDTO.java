package com.project.simplegw.approval.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TemplateLineDetailsDTO {
    // server --> view only
    private List<ApproverDTO> approverList;
    private List<ReferrerDTO> referrerList;
}
