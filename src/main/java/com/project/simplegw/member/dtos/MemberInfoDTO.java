package com.project.simplegw.member.dtos;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MemberInfoDTO extends MemberDTO {
    private String nameEng;
    private String mobileNo;
    private String mailAddress;
    private String tel;
    private LocalDate dateHire;
    private String serviceDays;

    private byte[] imgSrc;   // 임직원 현황 메뉴에서 리스트 불러올 때 사진 파일을 함께 전송하기 위해 추가.
    private LocalDate birthday;
    private int age;         // 임직원 현황 메뉴에서 나이를 보여주기 위해 추가.

    // 생일 값을 입력받기 위해 추가
    private int year;
    private int month;
    private int day;

    public MemberInfoDTO updateServiceDays() {
        if(dateHire != null) {
            Period period = Period.between(dateHire, LocalDate.now());
            this.serviceDays = period.getYears() +  "년 " + period.getMonths() + "개월";
        }
        return this;
    }

    public MemberInfoDTO bindingImgSrc(byte[] imgSrc) {
        this.imgSrc = imgSrc;
        return this;
    }

    public boolean updateBirthday() {
        try {
            this.birthday = LocalDate.of(year, month, day);
            return true;
        } catch(DateTimeException e) {
            return false;    
        }
    }
}
