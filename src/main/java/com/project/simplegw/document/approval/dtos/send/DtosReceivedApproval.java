package com.project.simplegw.document.approval.dtos.send;

import com.project.simplegw.document.dtos.send.DtosDocsMin;
import com.project.simplegw.document.vos.DocsGroup;
import com.project.simplegw.document.vos.DocsType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
public class DtosReceivedApproval extends DtosDocsMin {
    // 결재로 받은 문서 리스트를 보기 위한 dto
    private String typeTitle;   // 결재문서 타입.

    public DtosReceivedApproval setType(DocsType type) {
        if(type.getGroup() == DocsGroup.APPROVAL)
            this.typeTitle = type.getTitle();
        return this;
    }
}
