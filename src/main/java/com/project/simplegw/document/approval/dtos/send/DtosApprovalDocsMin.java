package com.project.simplegw.document.approval.dtos.send;

import java.time.LocalDate;

import com.project.simplegw.document.approval.vos.Sign;
import com.project.simplegw.document.vos.DocsType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosApprovalDocsMin {
    // 결재문서 리스트 페이지에서 보여줄 최소한의 데이터만 가진 dto
    
    private Long id;
    private DocsType type;
    private String typeTitle;
    private boolean useEditors;
    private String title;

    private String writerTeam;
    private String writerJobTitle;
    private String writerName;

    private String approverTeam;
    private String approverJobTitle;
    private String approverName;

    private Sign sign;
    private LocalDate createdDate;

    private Long writerId;   // 관리자 검색 기능: 작성자 일치하는 문서만 집계할 때 필요하여 추가함.

    public DtosApprovalDocsMin updateDocsType(DocsType type) {
        this.type = type;
        this.typeTitle = type.getTitle();
        this.useEditors = type.useEditors();
        return this;
    }
}
