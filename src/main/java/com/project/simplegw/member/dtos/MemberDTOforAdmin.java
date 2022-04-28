package com.project.simplegw.member.dtos;

import com.project.simplegw.member.vos.MemberRole;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MemberDTOforAdmin {
    private Long id;

    private String userId;
    private String userPw;
    private MemberRole role;
    private boolean enabled;

    private String team;
    private String jobTitle;
    private String name;
    private boolean retired;
}
