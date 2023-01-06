package com.project.simplegw.system.dtos.receive;

import com.project.simplegw.system.vos.AuthorityValue;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorMenuAuthority {
    private boolean accessible;
    private AuthorityValue rwdRole;
    private AuthorityValue rwdOther;
}
