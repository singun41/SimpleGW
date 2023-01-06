package com.project.simplegw.member.dtos.admin.send;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class DtosMemberDetails extends DtosMember {
    private String nameEng;
    private String tel;
    private String mobile;
    private String email;
    private boolean emailUse;
    private boolean resigned;
    private LocalDate birthday;
    private LocalDate dateHire;
    private LocalDate dateResign;
}
