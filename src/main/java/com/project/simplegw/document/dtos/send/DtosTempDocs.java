package com.project.simplegw.document.dtos.send;

import java.time.LocalDate;
import java.time.LocalTime;

import com.project.simplegw.document.vos.DocsGroup;
import com.project.simplegw.document.vos.DocsType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosTempDocs {   // 임시저장한 문서 리스트를 보여줄 때 사용할 dto, 임시저장한 문서는 본인것만 보므로 작성자 정보는 불필요.
    private Long id;
    private DocsType type;
    private DocsGroup group;
    private boolean useEditors;
    private String typeTitle;
    private String title;
    private LocalDate createdDate;
    private LocalTime createdTime;

    public DtosTempDocs updateGroup() {
        this.group = type.getGroup();
        this.useEditors = type.useEditors();
        return this;
    }
}
