package com.project.simplegw.document.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString(exclude = "content")
@Accessors(chain = true)
public class DocsDTO {
    // view --> server 전송 데이터
    // 1. 문서 저장(insert) 요청시 : title, content, 나머지는 서버에서 바인딩
    // 2. 문서 디테일 요청시 : no, type, code
    // 3. 문서 업데이트 요청시 : no, title, content, 나머지는 서버에서 바인딩
    // 4. 문서 리스트 요청시 : no, type, code, title, writer변수들, 시간 변수들

    // server --> view data
    // 1. view에서 화면 디테일 요청시 전송 데이터: no, title, content, writer변수들, 시간 변수들

    private Long id;
    private Long memberId;
    private String type;
    private String kind;
    private String title;
    private String content;
    private String writerName;
    private String writerTeam;
    private String writerJobTitle;
    private LocalDate createdDate;
    private LocalTime createdTime;
    private LocalDateTime updatedDatetime;
    private boolean registered;

    private String kindTitle;   // 임시저장 리스트에서 문서 유형의 타이틀을 보여주기 위해 추가.
}
