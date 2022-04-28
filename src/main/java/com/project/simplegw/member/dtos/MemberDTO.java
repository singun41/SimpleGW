package com.project.simplegw.member.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MemberDTO {
    private Long id;
    private String team;
    private String jobTitle;
    private String name;
}
