package com.project.simplegw.document.approval.dtos.send.details.dayoff;

import java.util.List;

import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsCommon;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class DtosDayoff extends DtosApprovalDocsCommon {
    List<DtosDayoffDetails> details;
}
