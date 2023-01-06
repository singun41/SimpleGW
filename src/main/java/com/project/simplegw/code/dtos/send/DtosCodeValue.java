package com.project.simplegw.code.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosCodeValue {
    private String key;
    private String value;
}
