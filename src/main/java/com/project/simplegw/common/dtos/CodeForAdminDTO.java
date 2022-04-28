package com.project.simplegw.common.dtos;

import com.project.simplegw.common.vos.BasecodeType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CodeForAdminDTO {
    private Long id;
    private BasecodeType type;
    private String code;
    private String value;
    private boolean enabled;
    private String remarks;
    private int seq;

    // admin 메뉴 코드관리에서 type 리스트를 보기 위해 추가
    public static CodeForAdminDTO setBasecodeType(BasecodeType type) {
        return new CodeForAdminDTO().setType(type).setValue(type.getTitle());
    }
}
