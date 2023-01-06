package com.project.simplegw.document.approval.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosSavedLine {
    // 저장된 결재라인 타이틀만 프론트로 보내주는 dto
    
    private Long id;
    private String title;
}
