package com.project.simplegw.document.dtos.send;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosDocsTitle {   // 메인화면에 보여줄 공지사항, 자유게시판 리스트 전용 dto
    private Long id;
    private String title;
    private LocalDate createdDate;
    private boolean isNew;

    public DtosDocsTitle updateIsNew() {
        this.isNew = ChronoUnit.DAYS.between(this.createdDate, LocalDate.now()) <= 2L;   // 등록한 날부터 2일째 되는 날까지 true
        return this;
    }
}
