package com.project.simplegw.document.dtos.admin.receive;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorEditorForm {
    private String content;
}
