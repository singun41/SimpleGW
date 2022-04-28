package com.project.simplegw.common.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BasecodeDTO {
    private String code;
    private String value;
}
