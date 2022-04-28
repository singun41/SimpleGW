package com.project.simplegw.approval.dtos;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ReceivedDocsDTO {
    // 수신한 결재문서 리스트 전용 DTO
    // ApprovalService 클래스에서 바인딩 된다.
    // server --> view only
    private String kind;
    private Long docsId;
    private String writerJobTitle;
    private String writerName;
    private String title;
    private String status;
    private String approverJobTitle;
    private String approverName;
    private LocalDate createdDate;

    private String kindTitle;   // kind에 해당하는 enum의 title을 보여주기 위해 추가

    public ReceivedDocsDTO(Object[] arrObj) {
        this.kind = (String)arrObj[0];
        this.docsId = Long.parseLong(String.valueOf(arrObj[1]));
        this.writerJobTitle = (String)arrObj[2];
        this.writerName = (String)arrObj[3];
        this.title = (String)arrObj[4];
        this.status = (String)arrObj[5];
        this.approverJobTitle = (String)arrObj[6];
        this.approverName = (String)arrObj[7];
        this.createdDate = java.sql.Date.valueOf(String.valueOf(arrObj[8])).toLocalDate();
    }
}
