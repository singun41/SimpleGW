package com.project.simplegw.member.dtos.admin.send;

import com.project.simplegw.system.vos.Role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosMember {
    private Long id;
    private String userId;
    private Role role;
    private boolean enabled;
    
    private String team;
    private String jobTitle;
    private String name;
}
