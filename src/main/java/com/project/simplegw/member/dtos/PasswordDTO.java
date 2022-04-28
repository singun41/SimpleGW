package com.project.simplegw.member.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDTO {
    private Long id;   // member entitiy id
    private String originalPw;
    private String newPw;
    private String newPwCheck;
}
