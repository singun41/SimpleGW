package com.project.simplegw.document.dtos.send;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosDocsOptions {
    private LocalDate dueDate;
}
