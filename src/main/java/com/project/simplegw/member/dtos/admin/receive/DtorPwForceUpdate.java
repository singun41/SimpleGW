package com.project.simplegw.member.dtos.admin.receive;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.project.simplegw.system.vos.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(exclude = "pw")
public class DtorPwForceUpdate {
    // 관리자가 유저 계정 패스워드 강제 변경시 사용.

    @NotBlank(message = "패스워드를 입력하세요.")
    @Size(min = Constants.PW_UPDATE_AT_LEAST_LENGTH, message = "최소 " + Constants.PW_UPDATE_AT_LEAST_LENGTH + "자 이상, 영문, 숫자, 특수문자를 포함해 작성하세요.")
    @Pattern(regexp = Constants.REGEXP_PW)
    private String pw;
}
