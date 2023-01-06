package com.project.simplegw.member.dtos.send;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosEmployeesProfile {   // 임직원 정보 전용 dto
    private byte[] portrait;
    private String name;
    private String jobTitle;
    private String email;
    private String tel;
    private String mobile;
    private String duration;   // 근무기간(근속년수)
    private LocalDate dateHire;
}
