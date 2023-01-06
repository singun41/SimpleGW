package com.project.simplegw.member.dtos.send;

import java.time.LocalDate;
import java.time.Period;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DtosProfile {   // 유저가 본인 정보를 요청할 때 프론트로 데이터를 보내기 위한 dto
    private String team;
    private String jobTitle;
    private String name;
    private String nameEng;
    private String mobile;
    private String email;
    private String tel;
    private String duration;
    private LocalDate birthday;
    private LocalDate dateHire;

    public DtosProfile calcDuration() {   // 근무기간 계산하기
        if(this.dateHire != null) {
            Period period = Period.between(this.dateHire, LocalDate.now());
            this.duration = period.getYears() +  "년 " + period.getMonths() + "개월";
        }
        return this;
    }
}
