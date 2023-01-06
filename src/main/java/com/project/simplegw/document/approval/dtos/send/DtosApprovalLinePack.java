package com.project.simplegw.document.approval.dtos.send;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosApprovalLinePack {
    private List<DtosApprover> approvers;
    private List<DtosReferrer> referrers;
}
