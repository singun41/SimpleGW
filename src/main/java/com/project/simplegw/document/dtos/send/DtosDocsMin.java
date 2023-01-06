package com.project.simplegw.document.dtos.send;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DtosDocsMin {   // 공지사항, 자유게시판, 제안게시판, 자료실 등의 일반문서 리스트 페이지에서 보여줄 최소한의 데이터만 가진 dto
    private Long id;
    private String title;
    private String writerTeam;
    private String writerJobTitle;
    private String writerName;
    private LocalDate createdDate;
}
