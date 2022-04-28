package com.project.simplegw.document.dtos;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DocsOptionsDTO {
    private boolean use;
    private Long docsId;
    private LocalDate dueDate;
}
