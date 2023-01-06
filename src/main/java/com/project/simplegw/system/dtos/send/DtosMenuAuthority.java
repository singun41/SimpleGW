package com.project.simplegw.system.dtos.send;

import com.project.simplegw.system.vos.AuthorityValue;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.Role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosMenuAuthority {
    private Long id;
    private Menu menu;
    private Role role;
    private boolean accessible;
    private AuthorityValue rwdRole;
    private AuthorityValue rwdOther;
}
