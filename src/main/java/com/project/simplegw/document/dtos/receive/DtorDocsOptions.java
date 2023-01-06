package com.project.simplegw.document.dtos.receive;

import com.project.simplegw.system.helpers.DateValid;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorDocsOptions {   // 문서 옵션 설정시 프론트로부터 받는 데이터 dto, 업데이트시 docsId는 url로 받아야한다. (RestAPI 표준 설계를 따름.)
    
    private boolean use;

    @DateValid   // null 허용
    private String dueDate;
}
