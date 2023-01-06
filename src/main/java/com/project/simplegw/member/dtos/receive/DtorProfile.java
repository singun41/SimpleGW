package com.project.simplegw.member.dtos.receive;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.project.simplegw.system.helpers.DateValid;
import com.project.simplegw.system.vos.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorProfile {
    // 유저가 개인정보 수정시 사용.

    // null 허용
    @Size(max = Constants.COLUMN_LENGTH_NAME, message = "영문 이름을 " + Constants.COLUMN_LENGTH_NAME + " 자 이하로 작성하세요.")
    @Pattern(regexp = Constants.REGEXP_NAME_ENG, message = "영문 이름을 다시 작성하세요.")
    private String nameEng;

    @NotBlank(message = "핸드폰 번호를 입력하세요.")
    @Pattern(regexp = Constants.REGEXP_MOBILE_NO, message = "핸드폰 번호를 000-0000-0000 형식으로 입력하세요.")
    private String mobile;

    @DateValid   // null 허용
    private LocalDate birthday;
}
