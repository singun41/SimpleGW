package com.project.simplegw.member.dtos.receive;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.project.simplegw.system.vos.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DtorPwChange {
    @NotBlank(message = "기존 패스워드를 입력하세요.")
    private String oldPw;
    
    @NotBlank(message = "새로운 패스워드를 입력하세요.")
    @Size(min = Constants.PW_UPDATE_AT_LEAST_LENGTH, message = "최소 " + Constants.PW_UPDATE_AT_LEAST_LENGTH + "자 이상, 영문, 숫자, 특수문자를 포함해 작성하세요.")
    @Pattern(regexp = Constants.REGEXP_PW)
    private String newPw;
}
