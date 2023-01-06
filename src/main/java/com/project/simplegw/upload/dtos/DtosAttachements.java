package com.project.simplegw.upload.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DtosAttachements {
    private Long docsId;
    private int seq;
    private String conversionName;
    private String originalName;
}
