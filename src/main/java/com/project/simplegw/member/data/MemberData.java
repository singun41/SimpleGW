package com.project.simplegw.member.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class MemberData {   // MemberService 클래스에서 멤버 정보를 캐싱하기 위한 클래스
    private Long id;
    private String team;
    private String jobTitle;
    private String name;
    private boolean resigned;
}
