package com.project.simplegw.document.dtos;

import java.time.LocalDate;

import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DocsSearchDTO {
    private Long memberId;
    private DocumentType type;
    private DocumentKind kind;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private boolean registered;
}
