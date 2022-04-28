package com.project.simplegw.approval.dtos;

import java.time.LocalDate;

import com.project.simplegw.approval.vos.ApproverStatus;
import com.project.simplegw.document.vos.DocumentKind;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DocsApprovalDTO {
    // server --> view 결재문서 리스트 보내기 전용
    private Long id;
    private DocumentKind kind;   // 진행중인, 완결된 결재문서 리스트를 보여줄 때 문서 종류를 구분하기 위해 추가.
    private String title;
    private String approverTeam;
    private String approverJobTitle;
    private String approverName;
    private ApproverStatus status;
    private LocalDate createdDate;

    private String kindTitle;   // kind에 해당하는 enum의 title을 보여주기 위해 추가

    public DocsApprovalDTO() { }   // 아래 생성자를 추가했기 때문에 기본 생성자도 명시해준다.

    public DocsApprovalDTO(Object[] arrObj) {
        // 진행중인, 완결된 결재문서 리스트를 보여줄 때 nativeQuery 결과값을 바인딩 위해 추가.
        // ApprovalDocStatusRepository에서 결과값을 받은 뒤 ApprovalService에서 바인딩.
        this.id = Long.parseLong(String.valueOf(arrObj[0]));
        this.kind = DocumentKind.valueOf((String)arrObj[1]);
        this.title = (String)arrObj[2];
        this.approverTeam = (String)arrObj[3];
        this.approverJobTitle = (String)arrObj[4];
        this.approverName = (String)arrObj[5];
        this.status = ApproverStatus.valueOf((String)arrObj[6]);
        this.createdDate = java.sql.Date.valueOf(String.valueOf(arrObj[7])).toLocalDate();
    }
}
