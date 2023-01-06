package com.project.simplegw.code.dtos.send;

import com.project.simplegw.code.vos.BasecodeType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosBasecode {
    private Long id;
    private BasecodeType type;
    private String code;
    private String value;
    private int seq;
    private String remarks;
    private boolean enabled;
}
