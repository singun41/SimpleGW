package com.project.simplegw.schedule.dtos.admin.send;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtosColor {
    private String code;    // 기초코드 schedule의 type별로 등록한 코드
    private String value;   // 기초코드 value
    private String hexValue;   // 색상 hex 값
}
