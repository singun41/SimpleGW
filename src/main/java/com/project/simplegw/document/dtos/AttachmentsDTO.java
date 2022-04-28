package com.project.simplegw.document.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AttachmentsDTO {
    private Long docsId;
    private int seq;
    private String originalName;
    private String conversionName;
}
